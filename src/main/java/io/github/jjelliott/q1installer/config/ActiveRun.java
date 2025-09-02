package io.github.jjelliott.q1installer.config;

import jakarta.inject.Singleton;

@Singleton
public class ActiveRun {

  private RunMode runMode;

  public enum RunMode {
    TEXT_MENU, CONFIG_COMMAND, GUI_MENU, INSTALL_COMMAND
  }

  public RunMode runMode() {
    return runMode;
  }

  public void setRunMode(RunMode runMode) {
    this.runMode = runMode;
  }
}
