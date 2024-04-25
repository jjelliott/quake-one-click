package io.github.jjelliott.q1installer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class UserProps {
  private String quakeDirectoryPath;
  private String quakeEnginePath;
  String location;

  public UserProps(Properties properties, String location) {
    quakeDirectoryPath = properties.getProperty("quake.directory-path");
    quakeEnginePath = properties.getProperty("quake.engine-path");
    this.location = location;
  }

  public Properties toProperties() {
    var props = new Properties();
    props.setProperty("quake.directory-path", quakeDirectoryPath);
    props.setProperty("quake.engine-path", quakeEnginePath);
    return props;
  }

  private void write() {
    try (FileOutputStream out = new FileOutputStream(location)) {
      System.out.println("Writing configuration...");
      toProperties().store(out, null);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String getQuakeDirectoryPath() {
    return quakeDirectoryPath;
  }

  public void setQuakeDirectoryPath(String quakeDirectoryPath) {
    this.quakeDirectoryPath = quakeDirectoryPath;
    write();
  }

  public boolean hasQuakeDirectoryPath(){
    return quakeDirectoryPath != null && !quakeDirectoryPath.isEmpty();
  }

  public String getQuakeEnginePath() {
    return quakeEnginePath;
  }

  public void setQuakeEnginePath(String quakeEnginePath) {
    this.quakeEnginePath = quakeEnginePath;
    write();
  }
  public boolean hasQuakeEnginePath(){
    return quakeEnginePath != null && !quakeEnginePath.isEmpty();
  }

  public void setPaths(String directoryPath, String enginePath) {
    quakeEnginePath = enginePath;
    quakeDirectoryPath = directoryPath;
    write();
  }
}
