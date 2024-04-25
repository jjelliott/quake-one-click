package io.github.jjelliott.q1installer.os;

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
public class Windows implements HandlerInstaller, ConfigLocation {

  private final Scanner scanner;

  public Windows(Scanner scanner) {
    this.scanner = scanner;
  }

  @Override
  public void install() {
    try (var resource = Objects.requireNonNull(this.getClass().getClassLoader().getResource("q1package.reg")).openStream()) {
      var outPath = Path.of(getConfig() + "/cache/q1package.reg");
      var escapedPath = System.getProperty("user.dir")
          .replaceAll("\\\\", "\\\\\\\\") + "\\\\"; // XXX: this is gross
      var contents = new String(resource.readAllBytes(), StandardCharsets.UTF_8)
          .replace("%WORKDIR%", escapedPath);
      Files.writeString(outPath, contents, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    System.out.println("A prompt should pop up to write to the registry.");
    System.out.println("You may review " + getConfig() + "/cache/q1package.reg if you wish before continuing.");
    System.out.println("Press enter to continue when you are ready...");
    scanner.nextLine();
    try {
      Runtime.getRuntime().exec("cmd /c \"" + getConfig() + "/cache/q1package.reg\"");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getConfig() {
    return System.getProperty("user.home") + "/AppData/Local/q1-installer";
  }

  @Override
  public String getExampleQuakePath() {
    return "C:\\Quake";
  }

  @Override
  public String getExampleEnginePath() {
    return getExampleQuakePath() + "\\glquake.exe";
  }
}
