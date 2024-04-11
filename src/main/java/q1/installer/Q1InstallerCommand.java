package q1.installer;

import io.micronaut.configuration.picocli.PicocliRunner;

import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.HttpClientConfiguration;
import jakarta.inject.Inject;
import org.apache.commons.io.FilenameUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import q1.installer.unpack.Extractor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Time;
import java.util.*;

@Command(name = "q1-installer", description = "...",
    mixinStandardHelpOptions = true)
public class Q1InstallerCommand implements Runnable {

  public static String confDirPath = System.getProperty("user.home") + "/.q1-installer";

  @Inject
  List<Extractor> extractors;
  @Inject
  HttpClientConfiguration configuration;

  @Parameters
  List<String> args = new ArrayList<>();

  public static void main(String[] args) throws Exception {
    PicocliRunner.run(Q1InstallerCommand.class, args);
  }

  public void run() {
    if (args.isEmpty()) {
      System.out.println("Hi!");
      System.out.println("This will eventually be an installer / setup helper");
      return;
    }
    Timer.start("configuration initialization");
    var cacheDir = new File(confDirPath + "/cache");
    cacheDir.mkdirs();
    var userPropsFile = new File(confDirPath + "/props");
    var userProps = new Properties();
    try {
      userProps.load(new FileReader(userPropsFile));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    List<String> installed;
    try {
      installed = Files.readAllLines(Path.of(confDirPath + "/installed"));
    } catch (IOException e) {
      installed = new ArrayList<>();
    }
    Timer.stop();
    Timer.start("message parsing");
    var launchMessage = new LaunchMessage(args.get(0));
    Timer.stop();
    if (!installed.contains(launchMessage.url)) {
      try {
        Timer.start("downloading");
        String fileName = downloadFile(launchMessage);
        Timer.stop();
        Timer.start("extracting");
        extractors.stream()
            .filter(it -> it.handles(FilenameUtils.getExtension(fileName)))
            .findFirst().orElseThrow()
            .extract(confDirPath + "/cache/" + fileName);
        Timer.stop();
        Timer.start("copying");
        if (launchMessage.type.equals("mod")) {
          copyFolder(Path.of(confDirPath + "/cache/" + FilenameUtils.getBaseName(fileName) + "/" + launchMessage.modName), Path.of(userProps.get("quake.directory-path").toString() + "/" + launchMessage.modName));
        } else if (launchMessage.type.equals("map")) {
          for (String packageFile : launchMessage.files) {
            Files.copy(Path.of(confDirPath + "/cache/" + FilenameUtils.getBaseName(fileName) + "/" + packageFile), Path.of(userProps.get("quake.directory-path").toString() + "/" + launchMessage.modName + "/maps/" + packageFile));
          }
        }
        Timer.stop();

        Files.writeString(Path.of(confDirPath + "/installed"), (!installed.isEmpty() ? "\n" : "") + launchMessage.url, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
      } catch (IOException e) {
        System.out.println("exception thrown");
      }
    } else {
      System.out.println("Skipping install step because already installed");
    }
    if (launchMessage.action.equals("run")) {
      String enginePath = userProps.get("quake.engine-path").toString();
      try {
        String runCommand = enginePath;
        if (!launchMessage.modName.equals("id1")) {
          runCommand += " -game " + launchMessage.modName;
        }
        runCommand += " +map " + launchMessage.launchMap;
        Runtime.getRuntime().exec(runCommand, null, new File(userProps.get("quake.directory-path").toString()));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

    }

    boolean autoClose = userProps.containsKey("installer.auto-close") && (Boolean.parseBoolean(userProps.get("installer.auto-close").toString()));

    if (!autoClose) {
      Scanner scanner = new Scanner(System.in);
      System.out.println("Press enter key to close");
      scanner.nextLine();
    }

  }

  public static void copyFolder(Path src, Path dest) {
    try {
      Files.walk(src).forEach(s -> {
        try {
          Path d = dest.resolve(src.relativize(s));
          if (Files.isDirectory(s)) {
            if (!Files.exists(d))
              Files.createDirectory(d);
            return;
          }
          Files.copy(s, d);// use flag to override existing
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  String downloadFile(LaunchMessage launchMessage) {
    String fileName = "";
    if (launchMessage.url.endsWith(".zip")) {
      var urlSplit = launchMessage.url.split("/");
      fileName = urlSplit[urlSplit.length - 1];
    }
    try (BlockingHttpClient httpClient = HttpClient.create(new URL(launchMessage.url), configuration).toBlocking()) {
      Timer.start("initial web request");
      var response = httpClient.exchange(launchMessage.url, byte[].class);
      Timer.stop();
      if (response.status().getCode() >= 300 && response.status().getCode() < 400) {
        if (response.header("location") != null) {
          var newLocation = response.header("location");
          if (newLocation.endsWith(".zip")) {
            var urlSplit = newLocation.split("/");
            fileName = urlSplit[urlSplit.length - 1];
          }
          Timer.start("second web request");
          response = httpClient.exchange(newLocation, byte[].class);
          Timer.stop();
        }
      }
      if (fileName.equals("")) {
        var disposition = response.header("content-disposition");
        if (disposition.contains("attachment; filename=\"")) {
          fileName = disposition.replace("attachment; filename=\"", "").replace("\"", "");
        }
      }
      Timer.start("downloaded file write");
      Files.write(Path.of(confDirPath + "/cache/" + fileName), response.body(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
      Timer.stop();
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return fileName;
  }
}

class LaunchMessage {
  String command;
  String action;
  String url;
  String type;
  String modName;
  String launchMap;
  List<String> files;

  LaunchMessage(String command) {
    this.command = command;
    System.out.println("parsing command: " + command);
    String commandWithoutProtocol;
    action = "install";
    commandWithoutProtocol = command.replace("q1package:", "");

    var split = commandWithoutProtocol.split(",");
    url = split[0];
    if (split[1].equals("mod")) {
      type = split[1];
      modName = split[2];
      launchMap = split.length >= 4 ? split[3] : null;
    } else if (split[1].equals("map")) {
      type = split[1];
      modName = split[2];
      files = split[3].equals("auto") ? null : Arrays.asList(split[3].split(":"));
      launchMap = split.length >= 5 ? split[4] : null;
    }
    if (launchMap != null) {
      action = "run";
    }

  }
}

class Timer {
  static private long start;

  static void start(String message) {
    if (message == null) {
//      System.out.println("Starting timer");
    } else {
//      System.out.println("Starting " + message + " timer");
    }
    start = System.currentTimeMillis();
  }

  static void stop() {
    var end = System.currentTimeMillis();
//    System.out.println("Seconds elapsed: " + ((end - start) / 1000.0));
  }
}