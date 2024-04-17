package io.github.jjelliott.q1installer;

import java.util.Properties;

public class UserProps {
  String quakeDirectoryPath;
  String quakeEnginePath;
//  boolean installerAutoClose;
//  boolean installerPrintTimings;

//  private boolean getWithDefaultFalse(Properties properties, String key){
//    return Boolean.parseBoolean(properties.containsKey(key) ? properties.getProperty(key) : "false");
//  }

  public UserProps(Properties properties) {
    quakeDirectoryPath = properties.getProperty("quake.directory-path");
    quakeEnginePath = properties.getProperty("quake.engine-path");
//    installerAutoClose = getWithDefaultFalse(properties, "installer.auto-close");
//    installerPrintTimings = getWithDefaultFalse(properties, "installer.print-timings");
  }

  public Properties toProperties(){
    var props = new Properties();
    props.setProperty("quake.directory-path", quakeDirectoryPath);
    props.setProperty("quake.engine-path", quakeEnginePath);
//    if (installerAutoClose)    props.setProperty("installer.auto-close", String.valueOf(true));
//    if (installerPrintTimings) props.setProperty("installer.print-timings", String.valueOf(true));
    return props;
  }
}
