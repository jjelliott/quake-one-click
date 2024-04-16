package io.github.jjelliott.q1installer;

import io.github.jjelliott.q1installer.os.ConfigLocation;
import io.github.jjelliott.q1installer.os.HandlerInstaller;
import io.github.jjelliott.q1installer.unpack.Extractor;
import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.annotation.Configuration;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.commons.io.FilenameUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

@Command(name = "q1-installer", description = "...",
    mixinStandardHelpOptions = true)
public class Q1InstallerCommand implements Runnable {

  public static String confDirPath;

  UserProps userProps;
  Timer timer;

  @Inject
  Scanner scanner;

  @Inject
  ConfigLocation configLocation;

  @Inject
  List<Extractor> extractors;

  @Inject
  HandlerInstaller handlerInstaller;

  @Parameters
  List<String> args = new ArrayList<>();

  public static void main(String[] args) {
    PicocliRunner.run(Q1InstallerCommand.class, args);
  }

  void initConfig() {
    confDirPath = configLocation.getConfig();
    var cacheDir = new File(confDirPath + "/cache");
    cacheDir.mkdirs();
    var userPropsFile = new File(confDirPath + "/user.properties");
    if (!userPropsFile.exists()) {
      try {
        userPropsFile.createNewFile();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    var userProperties = new Properties();
    try {
      userProperties.load(new FileReader(userPropsFile));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    userProps = new UserProps(userProperties);

    timer = new Timer();
  }

  public void run() {

    initConfig();
    if (args.isEmpty()) {
      menu();
    } else if (userProps.quakeDirectoryPath == null || userProps.quakeEnginePath == null) {
      System.out.println("Paths not set, please run setup and set them.");
    } else {
      List<String> installed;
      try {
        installed = Files.readAllLines(Path.of(confDirPath + "/installed.list"));
      } catch (IOException e) {
        installed = new ArrayList<>();
      }
      timer.start("message parsing");
      var launchMessage = new LaunchMessage(args.get(0));
      timer.stop();
      if (!installed.contains(launchMessage.url)) {
        try {
          String fileName = downloadFile(launchMessage);
          timer.start("extracting");
          extractors.stream()
              .filter(it -> it.handles(FilenameUtils.getExtension(fileName)))
              .findFirst().orElseThrow()
              .extract(confDirPath + "/cache/" + fileName);
          timer.stop();
          timer.start("copying");
          Files.createDirectories(quakeDirectoryPath(launchMessage.modName + "/maps/"));
          Files.list(Path.of(confDirPath + "/cache/" + FilenameUtils.getBaseName(fileName) + "/")).forEach(packageFilePath -> {
            try {
              if (Files.isDirectory(packageFilePath)) {
                if (packageFilePath.getFileName().toString().equals(launchMessage.modName)) {
                  copyFolder(packageFilePath, quakeDirectoryPath(launchMessage.modName));
                } else {
                  copyFolder(packageFilePath, quakeDirectoryPath(launchMessage.modName + "/" + packageFilePath.getFileName()));
                }
              } else {
                Files.copy(packageFilePath, quakeDirectoryPath(launchMessage.modName + (launchMessage.type.equals("map") ? "/maps/" : "/") + packageFilePath.getFileName().toString()));
              }
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          });
          timer.stop();

          Files.writeString(Path.of(confDirPath + "/installed"), (!installed.isEmpty() ? "\n" : "") + launchMessage.url, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        } catch (IOException e) {
          System.out.println("exception thrown");
        }
      } else {
        System.out.println("Skipping install step because already installed");
      }
      if (launchMessage.action.equals("run")) {
        timer.start("launching game");
        try {
          String runCommand = userProps.quakeEnginePath;
          if (!launchMessage.modName.equals("id1")) {
            runCommand += " -game " + launchMessage.modName;
          }
          runCommand += " +map " + launchMessage.launchMap;
          Runtime.getRuntime().exec(runCommand, null, new File(userProps.quakeDirectoryPath));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
        timer.stop();
      }
    }
    if (!userProps.installerAutoClose) {
      System.out.println("Press enter key to close");
      scanner.nextLine();
    }

  }

  Path quakeDirectoryPath(String subPath) {
    return Path.of(userProps.quakeDirectoryPath + "/" + subPath);
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

    timer.start("initial web request");
    HttpResponse<byte[]> response;
    try {
      response = client.send(HttpRequest.newBuilder(URI.create(launchMessage.url)).build(), HttpResponse.BodyHandlers.ofByteArray());
    } catch (IOException | InterruptedException ex) {
      throw new RuntimeException(ex);
    }
    timer.stop();
    if (response.statusCode() >= 300 && response.statusCode() < 400) {
      if (!response.headers().allValues("location").isEmpty()) {
        var newLocation = response.headers().firstValue("location").orElseThrow();
        if (newLocation.endsWith(".zip")) {
          var urlSplit = newLocation.split("/");
          fileName = urlSplit[urlSplit.length - 1];
        }
        timer.start("second web request");
        try {
          response = client.send(HttpRequest.newBuilder(URI.create(newLocation)).build(), HttpResponse.BodyHandlers.ofByteArray());
        } catch (IOException | InterruptedException e) {
          throw new RuntimeException(e);
        }
        timer.stop();
      }
    }
    if (fileName.isEmpty()) {
      var disposition = response.headers().firstValue("content-disposition").orElseThrow();
      if (disposition.contains("attachment; filename=\"")) {
        fileName = disposition.replace("attachment; filename=\"", "").replace("\"", "");
      }
    }
    timer.start("downloaded file write");
    try {
      Files.write(Path.of(confDirPath + "/cache/" + fileName), response.body(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    timer.stop();

    return fileName;
  }

  void menu() {
    var menu = true;

    System.out.println("Welcome to the q1-installer setup menu.");
    while (menu) {
      System.out.println("Select an option:");
      System.out.println("1: Install handler");
      System.out.println("2: Set Quake paths");
      System.out.println("3: Toggle auto-close");
      System.out.println("X: Exit this menu");

      var input = scanner.nextLine();

      switch (input.toLowerCase()) {
        case "1" -> {
          System.out.println("Installing URL handler...");
          handlerInstaller.install();
        }
        case "2" -> {
          var pathMenu = true;
          while (pathMenu) {
            System.out.println("Enter Quake directory path: ");
            var quakeDirPath = scanner.nextLine();
            System.out.println("Enter Quake engine path: ");
            var quakeEnginePath = scanner.nextLine();

            System.out.println("Directory path: " + quakeDirPath);
            System.out.println("Engine path: " + quakeEnginePath);
            System.out.println("Does this look correct? (y/yes/n/no)");
            var answer = scanner.nextLine().toLowerCase();
            if (answer.equals("y") || answer.equals("yes")) {
              try (FileOutputStream out = new FileOutputStream(confDirPath + "/props")) {
                System.out.println("Writing configuration...");
                userProps.quakeDirectoryPath = quakeDirPath;
                userProps.quakeEnginePath = quakeEnginePath;
                userProps.toProperties().store(out, null);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
              pathMenu = false;

            }
          }

        }
        case "3" -> {
          try (FileOutputStream out = new FileOutputStream(confDirPath + "/props")) {
            System.out.println(userProps.installerAutoClose ? "Disabling auto-close" : "Enabling auto-close");
            userProps.installerAutoClose = !userProps.installerAutoClose;
            userProps.toProperties().store(out, null);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
        case "x" -> menu = false;
        default -> System.out.println("Invalid input, please try again.");
      }
    }
  }

  class Timer {
    private long start;


    void start(String message) {
      if (userProps.installerPrintTimings) {
        if (message == null) {
          System.out.println("Starting timer");
        } else {
          System.out.println("Starting " + message + " timer");
        }
        start = System.currentTimeMillis();
      }
    }

    void stop() {
      if (userProps.installerPrintTimings) {
        var end = System.currentTimeMillis();
        System.out.println("Seconds elapsed: " + ((end - start) / 1000.0));
      }
    }
  }

}

@Factory
class ScannerFactory {
  @Singleton
  Scanner scanner(){
    return new Scanner(System.in);
  }
}