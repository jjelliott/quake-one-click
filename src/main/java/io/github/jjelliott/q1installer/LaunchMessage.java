package io.github.jjelliott.q1installer;

public class LaunchMessage {
  String command;
  String action;
  String url;
  String type;
  String modName;
  String launchMap;
  LaunchMessage  modPackage;

  public LaunchMessage(String command) {
    this.command = command;
    String commandWithoutProtocol;
    action = "install";
    commandWithoutProtocol = command.replace("q1package:", "");

    var split = commandWithoutProtocol.split(",");
    url = split[0];
    switch (split[1]) {
      case "mod-folder", "gamedir", "map" -> {
        type = split[1];
        modName = split[2];
        launchMap = split.length >= 4 ? split[3] : null;
      }
      case "mod-map" ->{
        type = split[1];
        modName = split[2];
        modPackage = new LaunchMessage(split[3]);
        launchMap = split.length >= 5 ? split[4] : null;
      }
      case "custom" -> {
        type = split[1];
        modName = split[2];
        // TODO: json string?
        launchMap = split.length >= 5 ? split[4] : null;
      }
      default -> throw new IllegalStateException("Unexpected value: " + split[1]);
    }
    if (launchMap != null) {
      action = "run";
    }

  }
}
