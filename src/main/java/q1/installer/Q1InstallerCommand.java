package q1.installer;

import io.micronaut.configuration.picocli.PicocliRunner;
import jakarta.inject.Inject;
import org.apache.commons.io.FilenameUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import q1.installer.unpack.Extractor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

@Command(name = "q1-installer", description = "...",
    mixinStandardHelpOptions = true)
public class Q1InstallerCommand implements Runnable {

  public static String confDirPath = System.getProperty("user.home") + "/.q1-installer";

  @Inject
  List<Extractor> extractors;

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
    HttpClient client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .followRedirects(HttpClient.Redirect.NEVER)
        .build();

    Timer.start("initial web request");
    HttpResponse<byte[]> response = null;
    try {
      response = client.send(HttpRequest.newBuilder(URI.create(launchMessage.url)).build(), HttpResponse.BodyHandlers.ofByteArray());
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    } catch (InterruptedException ex) {
      throw new RuntimeException(ex);
    }
    Timer.stop();
    if (response.statusCode() >= 300 && response.statusCode() < 400) {
      if (!response.headers().allValues("location").isEmpty()) {
        var newLocation = response.headers().firstValue("location").orElseThrow();
        if (newLocation.endsWith(".zip")) {
          var urlSplit = newLocation.split("/");
          fileName = urlSplit[urlSplit.length - 1];
        }
        Timer.start("second web request");
        try {
          response = client.send(HttpRequest.newBuilder(URI.create(newLocation)).build(), HttpResponse.BodyHandlers.ofByteArray());
        } catch (IOException e) {
          throw new RuntimeException(e);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        Timer.stop();
      }
    }
    if (fileName.equals("")) {
      var disposition = response.headers().firstValue("content-disposition").orElseThrow();
      if (disposition.contains("attachment; filename=\"")) {
        fileName = disposition.replace("attachment; filename=\"", "").replace("\"", "");
      }
    }
    Timer.start("downloaded file write");
    try {
      Files.write(Path.of(confDirPath + "/cache/" + fileName), response.body(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Timer.stop();

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
      System.out.println("Starting timer");
    } else {
      System.out.println("Starting " + message + " timer");
    }
    start = System.currentTimeMillis();
  }

  static void stop() {
    var end = System.currentTimeMillis();
    System.out.println("Seconds elapsed: " + ((end - start) / 1000.0));
  }
}