package io.github.jjelliott.q1installer;

import io.github.jjelliott.q1installer.config.UserProps;
import io.github.jjelliott.q1installer.os.ConfigLocation;
import io.github.jjelliott.q1installer.os.ExamplePath;
import io.github.jjelliott.q1installer.os.HandlerInstaller;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Scanner;

@Singleton
public class Menu {

  private final UserProps userProps;
  private final Scanner scanner;
  private final ConfigLocation configLocation;
  private final HandlerInstaller handlerInstaller;
  private final ExamplePath examplePath;

  public Menu(UserProps userProps, Scanner scanner, ConfigLocation configLocation, HandlerInstaller handlerInstaller, ExamplePath examplePath) {
    this.userProps = userProps;
    this.scanner = scanner;
    this.configLocation = configLocation;
    this.handlerInstaller = handlerInstaller;
    this.examplePath = examplePath;
  }

  void mainMenu() {
    var menu = true;

    System.out.println("Welcome to the q1-installer setup menu.");
    while (menu) {

      var input = prompt("""
          Select an option:
          1: Install handler
          2: Set Quake paths
          3: Clear cache
          X: Exit this menu
          """);

      switch (input.toLowerCase()) {
        case "1" -> installHandler();
        case "2" -> pathMenu();
        case "3" -> {
          try (var fileStream = Files.walk(Path.of(configLocation.getCacheDir()))) {
            for (Path path : fileStream.sorted(Comparator.reverseOrder()).toList()) {
              Files.deleteIfExists(path);
            }
            Files.createDirectories(Path.of(configLocation.getCacheDir()));
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
        case "x" -> menu = false;
        default -> System.out.println("Invalid input, please try again.");
      }
    }
  }

  void pathMenu() {
    var pathMenu = true;
    while (pathMenu) {
      if (userProps.hasQuakeDirectoryPath() && userProps.hasQuakeEnginePath()) {
        printCurrentPaths();

        var change = prompt("Would you like to change the currently configured paths? (y/yes/n/no)").toLowerCase();
        if (change.equals("y") || change.equals("yes")) {
          pathMenu = false;
          changePathsMenu();
        } else {
          pathMenu = false;
        }
      } else {
        pathMenu = !initialPathEntry();
      }
    }
  }

  void changePathsMenu() {
    var first = true;
    var submenu = true;

    while (submenu) {
      if (!first) printCurrentPaths();
      first = false;
      var modify = prompt("""
          Which path would you like to modify?
          1: Quake Directory
          2: Quake Engine
          X: Done modifying
          """);
      switch (modify.toLowerCase()) {
        case "1" -> updateDirectory(directoryPrompt());
        case "2" -> updateEngine(enginePrompt());
        case "x" -> submenu = false;
      }
    }
  }

  String directoryPrompt() {
    return prompt("Enter Quake directory path (example: " + examplePath.quakeDir() + "): ");
  }

  void updateDirectory(String quakeDirPath) {
    System.out.println("New directory path: " + quakeDirPath);
    var confirm = prompt("Does this look correct? (y/yes/n/no)").toLowerCase();
    if (confirm.equals("y") || confirm.equals("yes")) {
      userProps.setQuakeDirectoryPath(quakeDirPath);
    }
  }

  String enginePrompt() {
    return prompt("Enter Quake engine path (example: " + examplePath.engine() + "): ");
  }

  void updateEngine(String quakeEnginePath) {
    System.out.println("New engine path: " + quakeEnginePath);
    var confirm = prompt("Does this look correct? (y/yes/n/no)").toLowerCase();
    if (confirm.equals("y") || confirm.equals("yes")) {
      userProps.setQuakeEnginePath(quakeEnginePath);
    }
  }

  boolean initialPathEntry() {
    var quakeDirPath = directoryPrompt();
    var quakeEnginePath = enginePrompt();

    System.out.println("Directory path: " + quakeDirPath);
    System.out.println("Engine path: " + quakeEnginePath);
    var answer = prompt("Does this look correct? (y/yes/n/no)").toLowerCase();
    if (answer.equals("y") || answer.equals("yes")) {
      userProps.setPaths(quakeDirPath, quakeEnginePath);

      return true;
    }
    return false;
  }

  String prompt(String message) {
    System.out.println(message);
    return scanner.nextLine();
  }

  void printCurrentPaths() {
    System.out.println("Current paths:");
    System.out.println("- Quake Directory: " + userProps.getQuakeDirectoryPath());
    System.out.println("- Quake Engine   : " + userProps.getQuakeEnginePath());
  }

  void installHandler() {
    System.out.println("Installing URL handler...");
    handlerInstaller.install();
  }

}
