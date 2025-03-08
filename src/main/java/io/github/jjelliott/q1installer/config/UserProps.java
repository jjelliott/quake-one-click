package io.github.jjelliott.q1installer.config;

import io.github.jjelliott.q1installer.Game;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class UserProps {

  private final GameProps quake;
  private final GameProps quake2;
  private int skill;
  String location;

  public UserProps(String dirPath, String enginePath) {
    quake = new GameProps("quake");
    quake2 = new GameProps("quake2");
    quake.setDirectoryPath(dirPath, false);
    quake.setEnginePath(enginePath, false);
    skill = 1;
  }

  public UserProps(Properties properties, String location) {
    quake = new GameProps("quake", properties);
    quake2 = new GameProps("quake2", properties);
    skill = Integer.parseInt(properties.getProperty("skill", "1"));
    this.location = location;
  }

  public Properties toProperties() {
    var props = new Properties();
    quake.addToProperties(props);
    quake2.addToProperties(props);
    props.setProperty("skill", Integer.toString(skill));
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

  public GameProps getQuake() {
    return quake;
  }

  public GameProps getQuake2() {
    return quake2;
  }

  public GameProps getGameProps(Game game) {
    return switch (game) {
      case QUAKE -> quake;
      case QUAKE2 -> quake2;
      default -> throw new RuntimeException("unsupported game found");
    };
  }


  public int getSkill() {
    return skill;
  }

  public void setSkill(int skill) {
    setSkill(skill, true);
  }

  public void setSkill(int skill, boolean write) {
    this.skill = skill;
    if (write) {
      write();
    }
  }

  public class GameProps {

    private String directoryPath = "unset";
    private String enginePath = "unset";
    private String prefix;

    public GameProps(String prefix) {
      this.prefix = prefix;
    }

    public GameProps(String prefix, Properties properties) {
      this.prefix = prefix;
      directoryPath = properties.getProperty("%s.directory-path".formatted(prefix), "unset");
      enginePath = properties.getProperty("%s.engine-path".formatted(prefix), "unset");
    }

    void addToProperties(Properties properties) {
      properties.setProperty("%s.directory-path".formatted(prefix), directoryPath);
      properties.setProperty("%s.engine-path".formatted(prefix), enginePath);
    }

    public String getDirectoryPath() {
      return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
      setDirectoryPath(directoryPath, true);
    }

    public void setDirectoryPath(String directoryPath, boolean write) {
      this.directoryPath = directoryPath;
      if (write) {
        write();
      }
    }

    public boolean hasDirectoryPath() {
      return directoryPath != null && !directoryPath.isEmpty() && !directoryPath.equals("unset");
    }

    public String getEnginePath() {
      return enginePath;
    }

    public void setEnginePath(String enginePath) {
      setEnginePath(enginePath, true);
    }

    public void setEnginePath(String enginePath, boolean write) {
      this.enginePath = enginePath;
      if (write) {
        write();
      }
    }

    public boolean hasEnginePath() {
      return enginePath != null && !enginePath.isEmpty() && !enginePath.equals("unset");
    }

    public void setPaths(String directoryPath, String enginePath) {
      this.enginePath = enginePath;
      this.directoryPath = directoryPath;
      write();
    }
  }
}
