package io.github.jjelliott.q1installer;

import io.github.jjelliott.q1installer.config.ActiveRun;
import io.github.jjelliott.q1installer.config.ActiveRun.RunMode;
import io.github.jjelliott.q1installer.config.UserProps;
import io.github.jjelliott.q1installer.console.MainMenu;
import io.github.jjelliott.q1installer.error.ExitCodeException;
import io.github.jjelliott.q1installer.gui.Gui;
import io.micronaut.configuration.picocli.PicocliRunner;
import jakarta.inject.Inject;
import java.util.Scanner;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = "quake-one-click",
    description = "...",
    mixinStandardHelpOptions = true,
    subcommands = {ConfigCommand.class, ConsoleCommand.class})
public class Q1InstallerCommand implements Runnable {

  @Inject
  UserProps userProps;

  @Inject
  MainMenu mainMenu;

  @Inject
  ActiveRun activeRun;

  @Inject
  Q1Installer installer;

  @Inject
  Scanner scanner;

  @Inject
  Gui gui;

  @Parameters(index = "0", defaultValue = "")
  String arg;

  public static void main(String[] args) {
    PicocliRunner.run(Q1InstallerCommand.class, args);
  }

  public void run() {

    if (arg.isEmpty()) {
      //      menu.mainMenu();
      if (userProps.getMenuType().equals("gui")) {
        activeRun.setRunMode(RunMode.GUI_MENU);
        imgui.app.Application.launch(gui);
      } else if (userProps.getMenuType().equals("console")) {
        activeRun.setRunMode(RunMode.TEXT_MENU);
        mainMenu.show();
      }
    } else {
      activeRun.setRunMode(RunMode.INSTALL_COMMAND);
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
