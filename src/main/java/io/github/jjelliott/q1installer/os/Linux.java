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
public class Linux implements ConfigLocation, HandlerInstaller, ExamplePath {
  @Override
  public String getConfig() {
    return "%s/.config/quake-one-click".formatted(System.getProperty("user.home"));
  }

  @Override
  public String quakeDir() {
    return "/home/username/Games/quake";
  }

  @Override
  public String engine() {
    return quakeDir() + "/ironwail";
  }

  @Override
  public void install() {
    installHandler("q1package");
    installHandler("q2package");
  }

  private void installHandler(String protocol){
    var userHome = System.getProperty("user.home");
    try (var resource = Objects.requireNonNull(this.getClass().getClassLoader().getResource("%s.desktop".formatted(protocol))).openStream()) {
      Files.createDirectories(Path.of("%s/.local/share/applications/".formatted(userHome)));
      Files.write(Path.of("%s/.local/share/applications/%s.desktop".formatted(userHome, protocol)), resource.readAllBytes(), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    try {
      Runtime.getRuntime().exec("xdg-mime default %s.desktop x-scheme-handler/%s".formatted(protocol, protocol));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
