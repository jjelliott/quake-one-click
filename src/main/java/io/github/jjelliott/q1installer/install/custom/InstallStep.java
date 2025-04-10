package io.github.jjelliott.q1installer.install.custom;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class InstallStep {
  private final Action action;
  private final String filePath;
  private final String destFilePath;

  public InstallStep(Action action, String filePath, String destFilePath) {
    this.action = action;
    this.filePath = filePath;
    this.destFilePath = destFilePath;
  }

  public Action action() {
    return action;
  }

  public String filePath() {
    return filePath;
  }

  public String destFilePath() {
    return destFilePath;
  }
}
