package io.github.jjelliott.q1installer.config;

import io.github.jjelliott.q1installer.os.ConfigLocation;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Factory
public class PropsFactory {

  private final ConfigLocation configLocation;

  public PropsFactory(ConfigLocation configLocation) {
    this.configLocation = configLocation;
  }

  void createFileIfNotExists(String path) throws IOException {
    var pathObject = Path.of(path);
    if (Files.notExists(pathObject)) {
      Files.createFile(pathObject);
    }
  }

  void createRequiredFiles() throws IOException {
    Files.createDirectories(Path.of(configLocation.getCacheDir()));
    createFileIfNotExists(configLocation.getUserPropertiesFile());
    createFileIfNotExists(configLocation.getInstalledList());
  }

  @Singleton
  UserProps userProps() throws IOException {
    createRequiredFiles();

    var userProperties = new Properties();
    userProperties.load(Files.newInputStream(Path.of(configLocation.getUserPropertiesFile())));
    return new UserProps(userProperties, configLocation.getUserPropertiesFile());
  }

  @Singleton
  List<InstalledPackage> installed() throws IOException {
    createRequiredFiles();
    List<String> installed;
    try {
      installed = Files.readAllLines(Path.of(configLocation.getInstalledList()));
    } catch (IOException e) {
      installed = new ArrayList<>();
    }
    return installed.stream().map(InstalledPackage::new).toList();
  }
}
