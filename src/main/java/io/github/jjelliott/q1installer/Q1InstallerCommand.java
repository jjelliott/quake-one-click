package io.github.jjelliott.q1installer;

import io.github.jjelliott.q1installer.os.ConfigLocation;
import io.github.jjelliott.q1installer.os.HandlerInstaller;
import io.github.jjelliott.q1installer.unpack.Extractor;
import io.micronaut.configuration.picocli.PicocliRunner;
import jakarta.inject.Inject;
import org.apache.commons.io.FilenameUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

@Command(name = "q1-installer", description = "...",
    mixinStandardHelpOptions = true)
public class Q1InstallerCommand implements Runnable {

  List<String> installed;
  UserProps userProps;

  @Inject
  Scanner scanner;

  @Inject
  HttpClient client;

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

  void createFileIfNotExists(String path) throws IOException {
    var pathObject = Path.of(path);
    if (Files.notExists(pathObject)) {
      Files.createFile(pathObject);
    }
  }

  void createRequiredFiles() throws IOException {
    Files.createDirectories(Path.of(configLocation.getCacheDir()));
    createFileIfNotExists(configLocation.getUserPropertiesFile());
    createFileIfNotExists(configLocation.getInstalledList());
  }

  void initConfig() throws IOException {

    createRequiredFiles();

    var userProperties = new Properties();
    userProperties.load(Files.newInputStream(Path.of(configLocation.getUserPropertiesFile())));
    userProps = new UserProps(userProperties);

    try {
      installed = Files.readAllLines(Path.of(configLocation.getInstalledList()));
    } catch (IOException e) {
      installed = new ArrayList<>();
    }

  }

  public void run() {

    doOrExit(this::initConfig, "Unable to create / load configuration files", 1);

    if (args.isEmpty()) {
      menu();
    } else if (userProps.quakeDirectoryPath == null || userProps.quakeEnginePath == null) {
      System.out.println("Paths not set, please run setup and set them.");
    } else {
      var launchMessage = new LaunchMessage(args.get(0));
      if (!installed.contains(launchMessage.url)) {
        doOrExit(() -> installPackage(launchMessage), "Failed to install package", 2);
      } else {
        System.out.println("Skipping install step because already installed");
      }
      if (launchMessage.action.equals("run")) {
        doOrExit(() -> launchGame(launchMessage), "Unable to launch game", 3);
      }
    }
  }

  void doOrExit(ExceptionRunnable fn, String message, int code) {
    try {
      fn.run();
    } catch (Exception e) {
      System.out.println(message);
      e.printStackTrace();
      System.out.println("Press enter to close...");
      scanner.nextLine();
      System.exit(code);
    }
  }

  void installPackage(LaunchMessage launchMessage) throws IOException, InterruptedException {

    String fileName = downloadFile(launchMessage);
    extractors.stream()
        .filter(it -> it.handles(FilenameUtils.getExtension(fileName)))
        .findFirst().orElseThrow()
        .extract(configLocation.getCacheDirFile(fileName));
    Files.createDirectories(quakeDirectoryPath(launchMessage.modName));
    if (launchMessage.type.equals("map")) {
      Files.createDirectories(quakeDirectoryPath(launchMessage.modName + "/maps/"));
    }
    try (var fileStream = Files.list(Path.of(configLocation.getCacheDirFile(FilenameUtils.getBaseName(fileName) + "/")))){
    for (Path packageFilePath : fileStream.toList()) {
      if (Files.isDirectory(packageFilePath)) {
        if (packageFilePath.getFileName().toString().toLowerCase().equals(launchMessage.modName)) {
          copyFolder(packageFilePath, quakeDirectoryPath(launchMessage.modName));
        } else {
          copyFolder(packageFilePath, quakeDirectoryPath(launchMessage.modName + "/" + packageFilePath.getFileName().toString().toLowerCase()));
        }
      } else {
        Files.copy(packageFilePath, quakeDirectoryPath(launchMessage.modName + (launchMessage.type.equals("map") ? "/maps/" : "/") + packageFilePath.getFileName().toString().toLowerCase()), StandardCopyOption.REPLACE_EXISTING);
      }
    }
    }

    Files.writeString(Path.of(configLocation.getInstalledList()), (!installed.isEmpty() ? "\n" : "") + launchMessage.url, StandardOpenOption.WRITE, StandardOpenOption.APPEND);

  }

  void launchGame(LaunchMessage launchMessage) throws IOException {
    List<String> commandList = new ArrayList<>();
    commandList.add(userProps.quakeEnginePath);
    if (!launchMessage.modName.equals("id1")) {
      commandList.add("-game " + launchMessage.modName);
    }
    commandList.add("+map " +launchMessage.launchMap);
    var launched = Runtime.getRuntime().exec(commandList.toArray(new String[0]), null, new File(userProps.quakeDirectoryPath));

  }

  Path quakeDirectoryPath(String subPath) {
    return Path.of(userProps.quakeDirectoryPath + "/" + subPath);
  }

  public static void copyFolder(Path src, Path dest) throws IOException {
    try (var files = Files.walk(src)) {
      for (Path s : files.toList()) {
        Path d = dest.resolve(src.relativize(s));
        if (Files.isDirectory(s)) {
          if (!Files.exists(d))
            Files.createDirectory(d);
        } else {
          Files.copy(s, d, StandardCopyOption.REPLACE_EXISTING);// use flag to override existing
        }
      }
    }
  }

  String downloadFile(LaunchMessage launchMessage) throws IOException, InterruptedException {
    String fileName = "";
    if (launchMessage.url.endsWith(".zip")) {
      var urlSplit = launchMessage.url.split("/");
      fileName = urlSplit[urlSplit.length - 1];
    }

    HttpResponse<byte[]> response;

    response = client.send(HttpRequest.newBuilder(URI.create(launchMessage.url)).build(), HttpResponse.BodyHandlers.ofByteArray());

    if (response.statusCode() >= 300 && response.statusCode() < 400) {
      if (!response.headers().allValues("location").isEmpty()) {
        launchMessage.url = response.headers().firstValue("location").orElseThrow();
        return downloadFile(launchMessage);
      }
    }
    if (fileName.isEmpty()) {
      var disposition = response.headers().firstValue("content-disposition").orElseThrow();
      if (disposition.contains("attachment; filename=\"")) {
        fileName = disposition.replace("attachment; filename=\"", "").replace("\"", "");
      }
    }
    Files.write(Path.of(configLocation.getCacheDirFile(fileName)), response.body(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

    return fileName;
  }

  void menu() {
    var menu = true;

    System.out.println("Welcome to the q1-installer setup menu.");
    while (menu) {
      System.out.println("Select an option:");
      System.out.println("1: Install handler");
      System.out.println("2: Set Quake paths");
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
              userProps.quakeDirectoryPath = quakeDirPath;
              userProps.quakeEnginePath = quakeEnginePath;
              writeProperties();

              pathMenu = false;

            }
          }

        }
        case "x" -> menu = false;
        default -> System.out.println("Invalid input, please try again.");
      }
    }
  }

  void writeProperties() {
    try (FileOutputStream out = new FileOutputStream(configLocation.getUserPropertiesFile())) {
      System.out.println("Writing configuration...");
      userProps.toProperties().store(out, null);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}

@FunctionalInterface
interface ExceptionRunnable extends Runnable {
  @Override
  default void run() {
    try {
      runThrows();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  void runThrows() throws Exception;
}

