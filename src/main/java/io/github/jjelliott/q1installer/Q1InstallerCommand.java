package io.github.jjelliott.q1installer;

import io.github.jjelliott.q1installer.config.UserProps;
import io.github.jjelliott.q1installer.error.ExitCodeException;
import io.micronaut.configuration.picocli.PicocliRunner;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.Scanner;

@Command(name = "q1-installer", description = "...",
    mixinStandardHelpOptions = true)
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
    } else if (!userProps.hasQuakeDirectoryPath() || !userProps.hasQuakeEnginePath()) {
      System.out.println("Paths not set, please run setup and set them.");
      System.out.println("Press enter to close...");
      scanner.nextLine();
    } else {
      try {
        installer.run(new InstallerArguments(arg));
      } catch (ExitCodeException e){
        System.out.println(e.getMessage());
        e.printStackTrace();
        System.out.println("Press enter to close...");
        scanner.nextLine();
        System.exit(e.getExitCode());

      }
    }
  }

}


