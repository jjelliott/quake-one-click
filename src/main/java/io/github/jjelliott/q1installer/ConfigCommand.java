package io.github.jjelliott.q1installer;

import io.github.jjelliott.q1installer.config.ActiveRun;
import io.github.jjelliott.q1installer.config.ActiveRun.RunMode;
import io.github.jjelliott.q1installer.config.UserProps;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "config")
public class ConfigCommand implements Runnable {

  @Inject
  ActiveRun activeRun;
  @Inject
  UserProps userProps;
  @Parameters(index = "0", defaultValue = "")
  String field;
  @Parameters(index = "1", defaultValue = "")
  String value;

  @Override
  public void run() {
    activeRun.setRunMode(RunMode.CONFIG_COMMAND);
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
