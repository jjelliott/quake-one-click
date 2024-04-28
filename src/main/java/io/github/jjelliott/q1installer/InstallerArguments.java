package io.github.jjelliott.q1installer;

public class InstallerArguments {
  private final String command;
  private final String action;
  private String url;
  private final String type;
  private final String modName;
  private final String launchMap;
  private final InstallerArguments modPackage;

  public InstallerArguments(String command) {
    String tempAction;
    this.command = command;
    String commandWithoutProtocol;
    tempAction = "install";
    commandWithoutProtocol = command.replace("q1package:", "");

    var split = commandWithoutProtocol.split(",");
    url = split[0];
    switch (split[1]) {
      case "root", "mod-folder", "gamedir", "map" -> {
        type = split[1].equals("mod-folder") ? "root" : split[1];
        modName = split[2];
        modPackage = null;
        launchMap = split.length >= 4 ? split[3] : null;
      }
      case "mod-gamedir", "mod-map" -> {
        type = split[1];
        modName = split[2];
        modPackage = new InstallerArguments(split[3].replaceAll("%7C", ",").replaceAll("\\|", ","));
        launchMap = split.length >= 5 ? split[4] : null;
      }
      case "custom" -> {
        type = split[1];
        modName = split[2];
        modPackage = null;
        // TODO: json string?
        launchMap = split.length >= 5 ? split[4] : null;
      }
      default -> throw new IllegalStateException("Unexpected value: " + split[1]);
    }
    if (launchMap != null) {
      tempAction = "run";
    }

    action = tempAction;
  }

  public String getCommand() {
    return command;
  }

  public String getAction() {
    return action;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getType() {
    return type;
  }

  public String getModName() {
    return modName;
  }

  public String getLaunchMap() {
    return launchMap;
  }

  public InstallerArguments getModPackage() {
    return modPackage;
  }

  @Override
  public String toString() {
    return command;
  }
}
