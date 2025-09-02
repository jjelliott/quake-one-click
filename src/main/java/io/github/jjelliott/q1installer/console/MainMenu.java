package io.github.jjelliott.q1installer.console;

import io.github.jjelliott.q1installer.config.UserProps;
import io.github.jjelliott.q1installer.os.CacheOperations;
import io.github.jjelliott.q1installer.os.HandlerInstaller;
import jakarta.inject.Singleton;
import java.util.Scanner;

@Singleton
public class MainMenu implements ConsoleMenu {

  private final UserProps userProps;
  private final PathMenu pathMenu;
  private final SkillMenu skillMenu;
  private final Scanner scanner;
  private final HandlerInstaller handlerInstaller;
  private final CacheOperations cacheOperations;

  public MainMenu(
      UserProps userProps,
      PathMenu pathMenu,
      SkillMenu skillMenu,
      Scanner scanner,
      HandlerInstaller handlerInstaller,
      CacheOperations cacheOperations) {
    this.userProps = userProps;
    this.pathMenu = pathMenu;
    this.skillMenu = skillMenu;
    this.scanner = scanner;
    this.handlerInstaller = handlerInstaller;
    this.cacheOperations = cacheOperations;
  }

  public void show() {
    var menu = true;

    System.out.println("Welcome to the quake-one-click setup menu.");
    while (menu) {

      var input = prompt(
          """
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
        case "2" -> pathMenu.show(userProps.getQuake());
        case "3" -> pathMenu.show(userProps.getQuake2());
        case "4" -> skillMenu.show();
        case "5" -> cacheOperations.clearCache();
        case "x" -> menu = false;
        default -> System.out.println("Invalid input, please try again.");
      }
    }
  }

  @Override
  public Scanner getScanner() {
    return scanner;
  }

  void installHandler() {
    System.out.println("Installing URL handler...");
    handlerInstaller.install();
  }
}
