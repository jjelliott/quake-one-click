package io.github.jjelliott.q1installer;

import io.github.jjelliott.q1installer.config.UserProps;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Singleton
public class ProcessBuilderGameLauncher implements GameLauncher {
  private final UserProps userProps;
  private final Scanner scanner;

  public ProcessBuilderGameLauncher(UserProps userProps, Scanner scanner) {
    this.userProps = userProps;
      this.scanner = scanner;
  }

  List<String> generateLaunchCommand(InstallerArguments installerArguments) {
    int skill = userProps.getSkill();
    if (skill == -1){
      System.out.println("Please enter a skill number:\nEasy - 0, Normal - 1, Hard - 2, Nightmare - 3");
      skill = scanner.nextInt();
    }
    var modName = installerArguments.getModName();
    List<String> commandList = new ArrayList<>();

    commandList.add(userProps.getQuake().getEnginePath());
    commandList.add("-basedir");
    commandList.add(userProps.getQuake().getDirectoryPath());
    if (!modName.equals("id1")) {
      commandList.add("-game");
      commandList.add(modName);
    }
    if (skill != 1){
      commandList.add("+skill");
      commandList.add(Integer.toString(skill));
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
