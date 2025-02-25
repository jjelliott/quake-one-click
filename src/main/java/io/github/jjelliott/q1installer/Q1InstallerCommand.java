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
    } else if (arg.toLowerCase().startsWith("q1package")){
      if (!userProps.getQuake().hasDirectoryPath() || !userProps.getQuake().hasEnginePath()) {
        System.out.println("Paths not set, please run setup and set them.");
        System.out.println("Press enter to close...");
        scanner.nextLine();
      } else {
        try {
          installer.run(new InstallerArguments(arg));
        } catch (ExitCodeException e) {
          System.out.println(e.getMessage());
          e.printStackTrace();
          System.out.println("Press enter to close...");
          scanner.nextLine();
          System.exit(e.getExitCode());

        }
      }
    } else if (arg.toLowerCase().startsWith("q2package")){
      System.out.println("Quake 2 support coming soon...");
    }
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
    switch (field){
      case "q1-game-path" -> userProps.getQuake().setDirectoryPath(value);
      case "q1-engine-path" -> userProps.getQuake().setEnginePath(value);
      case "q2-game-path" -> userProps.getQuake2().setDirectoryPath(value);
      case "q2-engine-path" -> userProps.getQuake2().setEnginePath(value);
      case "skill" -> {
        try {
          userProps.setSkill(Integer.parseInt(value));
        } catch (NumberFormatException e){
          System.out.println("Invalid number format");
        }
        }
      }
  }
}

