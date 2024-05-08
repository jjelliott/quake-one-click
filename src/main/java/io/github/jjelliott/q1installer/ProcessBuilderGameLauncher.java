package io.github.jjelliott.q1installer;

import io.github.jjelliott.q1installer.config.UserProps;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ProcessBuilderGameLauncher implements GameLauncher {
  private final UserProps userProps;

  public ProcessBuilderGameLauncher(UserProps userProps) {
    this.userProps = userProps;
  }

  List<String> generateLaunchCommand(InstallerArguments installerArguments) {
    var modName = installerArguments.getModName();
    List<String> commandList = new ArrayList<>();
    commandList.add(userProps.getQuakeEnginePath());
    commandList.add("-basedir");
    commandList.add(userProps.getQuakeDirectoryPath());
    if (!modName.equals("id1")) {
      commandList.add("-game");
      commandList.add(modName);
    }
    commandList.add("+map");
    commandList.add(installerArguments.getLaunchMap());
    return commandList;
  }

  public void launchGame(InstallerArguments installerArguments) throws IOException {
    var builder = new ProcessBuilder(generateLaunchCommand(installerArguments));
    builder.start();
  }
}
