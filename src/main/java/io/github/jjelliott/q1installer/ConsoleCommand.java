package io.github.jjelliott.q1installer;

import io.github.jjelliott.q1installer.config.ActiveRun;
import io.github.jjelliott.q1installer.config.ActiveRun.RunMode;
import io.github.jjelliott.q1installer.console.MainMenu;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;

@Command(name = "console-menu")
public class ConsoleCommand implements Runnable {

  @Inject
  ActiveRun activeRun;

  @Inject
  MainMenu mainMenu;

  @Override
  public void run() {
    activeRun.setRunMode(RunMode.TEXT_MENU);
    mainMenu.show();
  }
}
