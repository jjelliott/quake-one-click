package io.github.jjelliott.q1installer.os;

import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

@Singleton
@Requires(os = Requires.Family.LINUX)
public class Linux implements ConfigLocation, HandlerInstaller {
  @Override
  public String getConfig() {
    return System.getProperty("user.home") + "/.config/q1-installer";
  }

  @Override
  public void install() {
    try (var resource = Objects.requireNonNull(this.getClass().getClassLoader().getResource("q1package.desktop")).openStream()) {
      Files.createDirectories(Path.of(System.getProperty("user.home") + "/.local/share/applications/"));
      Files.write(Path.of(System.getProperty("user.home") + "/.local/share/applications/q1package.desktop"), resource.readAllBytes(), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    try {
      Runtime.getRuntime().exec("bash -c \"xdg-mime default q1package.desktop x-scheme-handler/q1package\"");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
