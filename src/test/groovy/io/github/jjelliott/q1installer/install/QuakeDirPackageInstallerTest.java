package io.github.jjelliott.q1installer.install;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.jjelliott.q1installer.Game;
import io.github.jjelliott.q1installer.config.UserProps;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QuakeDirPackageInstallerTest {

  @Test
  @DisplayName("Test installer creates Quake directory path correctly")
  void testQuakeDirectoryPath() {
    var props = new UserProps("/path/to/directory", "/path/to/directory/engine");
    var installer = new QuakeDirPackageInstaller(props, null, null, null);
    assertEquals(Path.of("/path/to/directory/mod"),
        installer.quakeDirectoryPath(Game.QUAKE, "mod"));
  }
}