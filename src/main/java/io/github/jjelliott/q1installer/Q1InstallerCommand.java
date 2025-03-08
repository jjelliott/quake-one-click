package io.github.jjelliott.q1installer;

import io.github.jjelliott.q1installer.config.UserProps;
import io.github.jjelliott.q1installer.error.ExitCodeException;
import io.micronaut.configuration.picocli.PicocliRunner;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.Scanner;

@Command(name = "quake-one-click", description = "...",
    mixinStandardHelpOptions = true, subcommands = {ConfigCommand.class})
public class Q1InstallerCommand implements Runnable {

  @Inject
  UserProps userProps;

  @Inject
  Menu menu;

  @Inject
  Q1Installer installer;

  @Inject
  Scanner scanner;

  @Parameters(index = "0", defaultValue = "")
  String arg;


  public static void main(String[] args) {
    PicocliRunner.run(Q1InstallerCommand.class, args);
  }

  public void run() {

    if (arg.isEmpty()) {
      menu.mainMenu();
    } else {
      var installerArgs = new InstallerArguments(arg);
      if (checkPathsSet(userProps.getGameProps(installerArgs.getGame()))) {
        try {
          installer.run(installerArgs);
        } catch (ExitCodeException e) {
          System.out.println(e.getMessage());
          e.printStackTrace();
          System.out.println("Press enter to close...");
          scanner.nextLine();
          System.exit(e.getExitCode());

        }
      }
    }

  }

  private boolean checkPathsSet(UserProps.GameProps gameProps) {
    if (!gameProps.hasDirectoryPath() || !gameProps.hasEnginePath()) {
      System.out.println("Paths not set, please run setup and set them.");
      System.out.println("Press enter to close...");
      scanner.nextLine();
      return false;
    }
    return true;
  }
}

@Command(name = "config")
class ConfigCommand implements Runnable {

  @Inject
  UserProps userProps;
  @Parameters(index = "0", defaultValue = "")
  String field;
  @Parameters(index = "1", defaultValue = "")
  String value;

  @Override
  public void run() {
    switch (field) {
      case "q1-game-path" -> userProps.getQuake().setDirectoryPath(value);
      case "q1-engine-path" -> userProps.getQuake().setEnginePath(value);
      case "q2-game-path" -> userProps.getQuake2().setDirectoryPath(value);
      case "q2-engine-path" -> userProps.getQuake2().setEnginePath(value);
      case "skill" -> {
        try {
          userProps.setSkill(Integer.parseInt(value));
        } catch (NumberFormatException e) {
          System.out.println("Invalid number format");
        }
      }
    }
  }
}

