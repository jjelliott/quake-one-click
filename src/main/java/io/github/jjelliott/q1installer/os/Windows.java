package io.github.jjelliott.q1installer.os;

import io.github.jjelliott.q1installer.ActiveRun;
import io.github.jjelliott.q1installer.ActiveRun.RunMode;
import io.github.jjelliott.q1installer.Game;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.Scanner;

@Singleton
@Requires(os = Requires.Family.WINDOWS)
public class Windows implements HandlerInstaller, ConfigLocation, ExamplePath {

  private final Scanner scanner;
  private final ActiveRun activeRun;

  public Windows(Scanner scanner, ActiveRun activeRun) {
    this.scanner = scanner;
    this.activeRun = activeRun;
  }

  @Override
  public String textPrompt() {
    return """
        A prompt should pop up to write to the registry.
        You may review %s/cache/q1package.reg if you wish before continuing.""".formatted(
        getConfig());
  }

  @Override
  public void install() {
    try (var resource = Objects.requireNonNull(
        this.getClass().getClassLoader().getResource("q1package.reg")).openStream()) {
      var outPath = Path.of(getConfig() + "/cache/q1package.reg");
      var escapedPath = System.getProperty("user.dir")
          .replaceAll("\\\\", "\\\\\\\\") + "\\\\"; // XXX: this is gross
      var contents = new String(resource.readAllBytes(), StandardCharsets.UTF_8)
          .replace("%WORKDIR%", escapedPath);
      Files.writeString(outPath, contents, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    if (activeRun.runMode() == RunMode.TEXT_MENU) {
      System.out.println(textPrompt());
      System.out.println("Press enter to continue when you are ready...");
      scanner.nextLine();
    }
    try {
      Runtime.getRuntime().exec("cmd /c \"" + getConfig() + "/cache/q1package.reg\"");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getConfig() {
    return System.getProperty("user.home") + "/AppData/Local/quake-one-click";
  }

  @Override
  public String gameDir(Game game) {
    return switch (game){
      case QUAKE -> "C:\\Quake";
      case QUAKE2 -> "C:\\Quake2";
      case UNSUPPORTED -> "you aren't supposed to be here";
    };
  }

  @Override
  public String engine(Game game) {
    return gameDir(game) + switch (game) {
      case QUAKE -> "\\glquake.exe";
      case QUAKE2 -> "\\yquake2.exe";
      case UNSUPPORTED -> "what are you doing with your life";
    };
  }
}
