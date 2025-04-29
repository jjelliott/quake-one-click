package io.github.jjelliott.q1installer.install;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileUtil {

  public static void copyFolder(Path src, Path dest) throws IOException {
    try (var files = Files.walk(src)) {
      for (Path s : files.toList()) {
        Path d = dest.resolve(src.relativize(s));
        if (Files.isDirectory(s)) {
          if (!Files.exists(d)) {
            Files.createDirectory(d);
          }
        } else {
          Files.copy(s, d, StandardCopyOption.REPLACE_EXISTING);// use flag to override existing
        }
      }
    }
  }
}
