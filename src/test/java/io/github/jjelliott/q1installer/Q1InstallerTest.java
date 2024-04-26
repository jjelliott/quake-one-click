package io.github.jjelliott.q1installer;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class Q1InstallerTest {

  @Test
  void testQuakeDirectoryPath(){
    var props = new UserProps("/path/to/directory", "/path/to/directory/engine");
    var installer = new Q1Installer(props, null,null,null,null,null,"null,map,null,null");
    assertEquals(Path.of("/path/to/directory/mod"), installer.quakeDirectoryPath("mod"));
  }
}