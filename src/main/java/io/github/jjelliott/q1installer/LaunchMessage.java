package io.github.jjelliott.q1installer;

import java.util.Arrays;
import java.util.List;

public class LaunchMessage {
  String command;
  String action;
  String url;
  String type;
  String modName;
  String launchMap;
  List<String> files;

  public LaunchMessage(String command) {
    this.command = command;
    System.out.println("parsing command: " + command);
    String commandWithoutProtocol;
    action = "install";
    commandWithoutProtocol = command.replace("q1package:", "");

    var split = commandWithoutProtocol.split(",");
    url = split[0];
    if (split[1].equals("mod-folder") || split[1].equals("gamedir")) {
      type = split[1];
      modName = split[2];
      launchMap = split.length >= 4 ? split[3] : null;
    } else if (split[1].equals("map")) {
      type = split[1];
      modName = split[2];
      files = split[3].equals("auto") ? null : Arrays.asList(split[3].split(":"));
      launchMap = split.length >= 5 ? split[4] : null;
    } else if (split[1].equals("custom")){
      type = split[1];
      modName = split[2];

      launchMap = launchMap = split.length >= 5 ? split[4] : null;
    }
    if (launchMap != null) {
      action = "run";
    }

  }
}
