package io.github.jjelliott.q1installer;

import io.github.jjelliott.q1installer.config.UserProps;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static io.github.jjelliott.q1installer.Game.QUAKE;
import static io.github.jjelliott.q1installer.Game.QUAKE2;

@Singleton
public class ProcessBuilderGameLauncher implements GameLauncher {

  private final UserProps userProps;
  private final Scanner scanner;

  public ProcessBuilderGameLauncher(UserProps userProps, Scanner scanner) {
    this.userProps = userProps;
    this.scanner = scanner;
  }

  List<String> generateLaunchCommand(InstallerArguments installerArguments) {
    var game = installerArguments.getGame();
    var gameProps = userProps.getGameProps(game);
    int skill = userProps.getSkill();
    if (skill == -1) {
      System.out.println(
          "Please enter a skill number:\nEasy - 0, Normal - 1, Hard - 2, Nightmare - 3");
      skill = scanner.nextInt();
    }
    var modName = installerArguments.getModName();
    List<String> commandList = new ArrayList<>();

    commandList.add(gameProps.getEnginePath());

    if (installerArguments.getGame() == QUAKE) {
      setBaseDir(commandList, gameProps, List.of("-basedir"));
      if (!modName.equals("id1")) {
        commandList.add("-game");
        commandList.add(modName);
      }
    } else if (installerArguments.getGame() == QUAKE2) {
      if (!gameProps.getDirectoryPath().contains("Saved Games")) {// q2ex doesn't like this
        setBaseDir(commandList, gameProps, List.of("+set", "basedir"));
      }
      if (!modName.equals("baseq2")) {
        commandList.add("+set");
        commandList.add("game");
        commandList.add(modName);
      }
    }
    if (skill != 1) {
      commandList.add("+skill");
      commandList.add(Integer.toString(skill));
    }
    commandList.add("+map");
    commandList.add(installerArguments.getLaunchMap());
    System.out.println(commandList);
    return commandList;
  }

  public void launchGame(InstallerArguments installerArguments) throws IOException {
    var builder = new ProcessBuilder(generateLaunchCommand(installerArguments));
    builder.start();
  }

  private void setBaseDir(List<String> commandList, UserProps.GameProps gameProps,
      List<String> commandsToAdd) {
    commandList.addAll(commandsToAdd);
    commandList.add(gameProps.getDirectoryPath());
  }
}
