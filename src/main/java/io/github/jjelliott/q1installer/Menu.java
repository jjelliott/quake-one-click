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
import java.util.List;
import java.util.Scanner;

@Singleton
public class Menu {

  private final UserProps userProps;
  private final Scanner scanner;
  private final ConfigLocation configLocation;
  private final HandlerInstaller handlerInstaller;
  private final ExamplePath examplePath;
  private final MenuOperations menuOperations;

  public Menu(UserProps userProps, Scanner scanner, ConfigLocation configLocation,
      HandlerInstaller handlerInstaller, ExamplePath examplePath, MenuOperations menuOperations) {
    this.userProps = userProps;
    this.scanner = scanner;
    this.configLocation = configLocation;
    this.handlerInstaller = handlerInstaller;
    this.examplePath = examplePath;
    this.menuOperations = menuOperations;
  }

  void mainMenu() {
    var menu = true;

    System.out.println("Welcome to the quake-one-click setup menu.");
    while (menu) {

      var input = prompt("""
          Select an option:
          1: Install handler
          2: Set Quake paths
          3: Set Quake 2 paths
          4: Set Skill
          5: Clear cache
          X: Exit this menu
          """);

      switch (input.toLowerCase()) {
        case "1" -> installHandler();
        case "2" -> pathMenu(userProps.getQuake());
        case "3" -> pathMenu(userProps.getQuake2());
        case "4" -> skillMenu();
        case "5" -> {
          menuOperations.clearCache();
        }
        case "x" -> menu = false;
        default -> System.out.println("Invalid input, please try again.");
      }
    }
  }

  void pathMenu(UserProps.GameProps game) {
    var pathMenu = true;
    while (pathMenu) {
      if (game.hasDirectoryPath() && game.hasEnginePath()) {
        printCurrentPaths(game);

        var change = prompt(
            "Would you like to change the currently configured paths? (y/yes/n/no)").toLowerCase();
        if (change.equals("y") || change.equals("yes")) {
          pathMenu = false;
          changePathsMenu(game);
        } else {
          pathMenu = false;
        }
      } else {
        pathMenu = !initialPathEntry(game);
      }
    }
  }

  void changePathsMenu(UserProps.GameProps game) {
    var first = true;
    var submenu = true;

    while (submenu) {
        if (!first) {
            printCurrentPaths(game);
        }
      first = false;
      var modify = prompt("""
          Which path would you like to modify?
          1: Quake Directory
          2: Quake Engine
          X: Done modifying
          """);
      switch (modify.toLowerCase()) {
        case "1" -> updateDirectory(game, directoryPrompt());
        case "2" -> updateEngine(game, enginePrompt());
        case "x" -> submenu = false;
      }
    }
  }

  String directoryPrompt() {
    return prompt("Enter Quake directory path (example: " + examplePath.quakeDir() + "): ");
  }

  void updateDirectory(UserProps.GameProps game, String quakeDirPath) {
    System.out.println("New directory path: " + quakeDirPath);
    var confirm = prompt("Does this look correct? (y/yes/n/no)").toLowerCase();
    if (confirm.equals("y") || confirm.equals("yes")) {
      game.setDirectoryPath(quakeDirPath, true);
    }
  }

  String enginePrompt() {
    return prompt("Enter Quake engine path (example: " + examplePath.engine() + "): ");
  }

  void updateEngine(UserProps.GameProps game, String quakeEnginePath) {
    System.out.println("New engine path: " + quakeEnginePath);
    var confirm = prompt("Does this look correct? (y/yes/n/no)").toLowerCase();
    if (confirm.equals("y") || confirm.equals("yes")) {
      game.setEnginePath(quakeEnginePath, true);
    }
  }

  boolean initialPathEntry(UserProps.GameProps game) {
    var quakeDirPath = directoryPrompt();
    var quakeEnginePath = enginePrompt();

    System.out.println("Directory path: " + quakeDirPath);
    System.out.println("Engine path: " + quakeEnginePath);
    var answer = prompt("Does this look correct? (y/yes/n/no)").toLowerCase();
    if (answer.equals("y") || answer.equals("yes")) {
      game.setPaths(quakeDirPath, quakeEnginePath);

      return true;
    }
    return false;
  }

  String prompt(String message) {
    System.out.println(message);
    return scanner.nextLine();
  }

  void printCurrentPaths(UserProps.GameProps game) {
    System.out.println("Current paths:");
    System.out.println("- Quake Directory: " + game.getDirectoryPath());
    System.out.println("- Quake Engine   : " + game.getEnginePath());
  }

  void installHandler() {
    System.out.println("Installing URL handler...");
    handlerInstaller.install();
  }

  void skillMenu() {
    var validInputs = List.of("0", "1", "2", "3", "a");
    var skillStr = prompt("""
        This is the skill that the app will launch by default.
        Please input just the number: 
        0 - Easy
        1 - Normal
        2 - Hard
        3 - Nightmare
        a - Ask every time
        """);
    if (validInputs.contains(skillStr)) {
        if (skillStr.equals("a")) {
            userProps.setSkill(-1);
        } else {
            userProps.setSkill(Integer.parseInt(skillStr));
        }
    } else {
      System.out.println("Invalid input, please try again.");
      skillMenu();
    }
  }

}
