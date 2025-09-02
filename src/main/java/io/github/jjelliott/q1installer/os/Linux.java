package io.github.jjelliott.q1installer.os;

import io.github.jjelliott.q1installer.config.Game;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Requires.Family;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

@Singleton
@Requires(os = Family.LINUX)
public class Linux implements ConfigLocation, HandlerInstaller, ExamplePath {

  @Override
  public String getConfig() {
    return "%s/.config/quake-one-click".formatted(System.getProperty("user.home"));
  }

  @Override
  public String gameDir(Game game) {
    return switch (game) {
      case QUAKE -> "/home/username/Games/quake";
      case QUAKE2 -> "/home/username/Games/quake2";
      case UNSUPPORTED -> "how tf did you get here";
    };
  }

  @Override
  public String engine(Game game) {
    return gameDir(game) + switch (game) {
      case QUAKE -> "/ironwail";
      case QUAKE2 -> "/yquake2";
      default -> "seriously how";
    };
  }

  @Override
  public String textPrompt() {
    var userHome = System.getProperty("user.home");
    return "The handler desktop files will be installed to %s/.local/share/applications/".formatted(
        userHome);
  }

  @Override
  public void install() {
    installHandler("q1package");
    installHandler("q2package");
  }

  private void installHandler(String protocol) {
    var userHome = System.getProperty("user.home");
    try (var resource = Objects.requireNonNull(
            this.getClass().getClassLoader().getResource("%s.desktop".formatted(protocol)))
        .openStream()) {
      Files.createDirectories(Path.of("%s/.local/share/applications/".formatted(userHome)));
      Files.write(Path.of("%s/.local/share/applications/%s.desktop".formatted(userHome, protocol)),
          resource.readAllBytes(), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    try {
      Runtime.getRuntime()
          .exec("xdg-mime default %s.desktop x-scheme-handler/%s".formatted(protocol, protocol));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
