package io.github.jjelliott.q1installer.console;

import io.github.jjelliott.q1installer.config.UserProps;
import io.github.jjelliott.q1installer.config.UserProps.GameProps;
import io.github.jjelliott.q1installer.os.ExamplePath;
import jakarta.inject.Singleton;
import java.util.Scanner;

@Singleton
public class PathMenu implements ConsoleMenu {
  private final Scanner scanner;
  private final ExamplePath examplePath;

  public PathMenu(Scanner scanner, ExamplePath examplePath) {
    this.scanner = scanner;
    this.examplePath = examplePath;
  }

  void show(UserProps.GameProps game) {
    var pathMenu = true;
    while (pathMenu) {
      if (game.hasDirectoryPath() && game.hasEnginePath()) {
        printCurrentPaths(game);

        var change = prompt("Would you like to change the currently configured paths? (y/yes/n/no)")
            .toLowerCase();
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
      var modify = prompt(
          """
          Which path would you like to modify?
          1: Quake Directory
          2: Quake Engine
          X: Done modifying
          """);
      switch (modify.toLowerCase()) {
        case "1" -> updateDirectory(game, directoryPrompt(game));
        case "2" -> updateEngine(game, enginePrompt(game));
        case "x" -> submenu = false;
      }
    }
  }

  String directoryPrompt(GameProps gameProps) {
    return prompt(
        "Enter Quake directory path (example: " + examplePath.gameDir(gameProps.game()) + "): ");
  }

  void updateDirectory(UserProps.GameProps game, String quakeDirPath) {
    System.out.println("New directory path: " + quakeDirPath);
    var confirm = prompt("Does this look correct? (y/yes/n/no)").toLowerCase();
    if (confirm.equals("y") || confirm.equals("yes")) {
      game.setDirectoryPath(quakeDirPath, true);
    }
  }

  String enginePrompt(GameProps gameProps) {
    return prompt(
        "Enter Quake engine path (example: " + examplePath.engine(gameProps.game()) + "): ");
  }

  void updateEngine(UserProps.GameProps game, String quakeEnginePath) {
    System.out.println("New engine path: " + quakeEnginePath);
    var confirm = prompt("Does this look correct? (y/yes/n/no)").toLowerCase();
    if (confirm.equals("y") || confirm.equals("yes")) {
      game.setEnginePath(quakeEnginePath, true);
    }
  }

  boolean initialPathEntry(UserProps.GameProps game) {
    var quakeDirPath = directoryPrompt(game);
    var quakeEnginePath = enginePrompt(game);

    System.out.println("Directory path: " + quakeDirPath);
    System.out.println("Engine path: " + quakeEnginePath);
    var answer = prompt("Does this look correct? (y/yes/n/no)").toLowerCase();
    if (answer.equals("y") || answer.equals("yes")) {
      game.setPaths(quakeDirPath, quakeEnginePath);

      return true;
    }
    return false;
  }

  void printCurrentPaths(UserProps.GameProps game) {
    System.out.println("Current paths:");
    System.out.println("- Quake Directory: " + game.getDirectoryPath());
    System.out.println("- Quake Engine   : " + game.getEnginePath());
  }

  @Override
  public Scanner getScanner() {
    return scanner;
  }
}
