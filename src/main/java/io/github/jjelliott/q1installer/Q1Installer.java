package io.github.jjelliott.q1installer;

import io.github.jjelliott.q1installer.os.ConfigLocation;
import io.github.jjelliott.q1installer.unpack.Extractor;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static io.github.jjelliott.q1installer.FileUtil.copyFolder;

public class Q1Installer {

  private final UserProps userProps;
  private final List<InstalledPackage> installed;

  private final Scanner scanner;
  private final HttpClient client;
  private final ConfigLocation configLocation;
  private final List<Extractor> extractors;

  private final LaunchMessage launchMessage;

  public Q1Installer(UserProps userProps, List<InstalledPackage> installed, Scanner scanner, HttpClient client, ConfigLocation configLocation, List<Extractor> extractors, String message) {
    this.userProps = userProps;
    this.installed = installed;
    this.scanner = scanner;
    this.client = client;
    this.configLocation = configLocation;
    this.extractors = extractors;
    launchMessage = new LaunchMessage(message);
  }


  public void run() {

    if (installed.stream().noneMatch(installedPackage -> installedPackage.sourceUrl().equals(launchMessage.url))) {
      doOrExit(() -> {
        var filename = downloadFile(launchMessage);
        installPackage(launchMessage, filename);
      }, "Failed to install package", 2);
    } else {
      System.out.println("Skipping install step because already installed");
    }
    if (launchMessage.action.equals("run")) {
      doOrExit(() -> launchGame(launchMessage), "Unable to launch game", 3);
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

  void installPackage(LaunchMessage launchMessage, String fileName) throws IOException, InterruptedException {

    extractors.stream()
        .filter(it -> it.handles(FilenameUtils.getExtension(fileName)))
        .findFirst().orElseThrow()
        .extract(configLocation.getCacheDirFile(fileName));
    Files.createDirectories(quakeDirectoryPath(launchMessage.modName));
    if (launchMessage.type.equals("map")) {
      Files.createDirectories(quakeDirectoryPath(launchMessage.modName + "/maps/"));
    }
    try (var fileStream = Files.list(Path.of(configLocation.getCacheDirFile(FilenameUtils.getBaseName(fileName) + "/")))) {
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

  List<String> generateLaunchCommand(LaunchMessage launchMessage){
    List<String> commandList = new ArrayList<>();
    commandList.add(userProps.getQuakeEnginePath());
    commandList.add("-basedir");
    commandList.add(userProps.getQuakeDirectoryPath());
    if (!launchMessage.modName.equals("id1")) {
      commandList.add("-game");
      commandList.add(launchMessage.modName);
    }
    commandList.add("+map");
    commandList.add(launchMessage.launchMap);
    return commandList;
  }

  void launchGame(LaunchMessage launchMessage) throws IOException {
    var builder = new ProcessBuilder(generateLaunchCommand(launchMessage));
    System.out.println(builder.start().pid());

  }

  Path quakeDirectoryPath(String subPath) {
    return Path.of(userProps.getQuakeDirectoryPath() + "/" + subPath);
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


}

