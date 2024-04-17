package io.github.jjelliott.q1installer;

import java.util.Properties;

public class UserProps {
  String quakeDirectoryPath;
  String quakeEnginePath;

  public UserProps(Properties properties) {
    quakeDirectoryPath = properties.getProperty("quake.directory-path");
    quakeEnginePath = properties.getProperty("quake.engine-path");
  }

  public Properties toProperties(){
    var props = new Properties();
    props.setProperty("quake.directory-path", quakeDirectoryPath);
    props.setProperty("quake.engine-path", quakeEnginePath);
    return props;
  }
}
