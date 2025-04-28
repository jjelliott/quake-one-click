package io.github.jjelliott.q1installer;

import io.github.jjelliott.q1installer.os.ConfigLocation;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

@Singleton
public class MenuOperations {

  private final ConfigLocation configLocation;

  public MenuOperations(ConfigLocation configLocation) {
    this.configLocation = configLocation;
  }

  public String getCacheSize() {
    long size = 0;
    try (var fileStream = Files.walk(Path.of(configLocation.getCacheDir()))) {
      for (Path path : fileStream.sorted(Comparator.reverseOrder()).toList()) {
        size += Files.size(path);
      }
      Files.createDirectories(Path.of(configLocation.getCacheDir()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    var sizes = new String[]{"B", "KiB", "MiB", "GiB"};
    var iters = 0;
    while (size > 1024) {
      size /= 1024;
      iters++;
    }
    return size + sizes[iters];
  }

  public void clearCache() {
    try (var fileStream = Files.walk(Path.of(configLocation.getCacheDir()))) {
      for (Path path : fileStream.sorted(Comparator.reverseOrder()).toList()) {
        Files.deleteIfExists(path);
      }
      Files.createDirectories(Path.of(configLocation.getCacheDir()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
