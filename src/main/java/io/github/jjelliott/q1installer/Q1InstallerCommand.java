package io.github.jjelliott.q1installer;

import io.micronaut.configuration.picocli.PicocliRunner;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "q1-installer", description = "...",
    mixinStandardHelpOptions = true)
public class Q1InstallerCommand implements Runnable {

  @Inject
  UserProps userProps;

  @Inject
  Menu menu;

  @Inject
  Q1InstallerFactory factory;

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
    } else {
      factory.get(arg).run();
    }
  }

}


