package io.github.jjelliott.q1installer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Installer arguments instantiate correctly")
class InstallerArgumentsTest {

  @Test
  @DisplayName("root level mod without a launch map")
  void test_mod_instantiation() {
    var msg = new InstallerArguments("q1package:https://example.com/mod.zip,root,testmod");
    assertAll(
        () -> assertEquals("install", msg.getAction()),
        () -> assertEquals("https://example.com/mod.zip", msg.getUrl()),
        () -> assertEquals("root", msg.getType()),
        () -> assertEquals("testmod", msg.getModName()),
        () -> assertNull(msg.getLaunchMap()));
  }

  @Test
  @DisplayName("root level mod with a launch map")
  void test_mod_instantiation_with_start_map() {
    var msg = new InstallerArguments("q1package:https://example.com/mod.zip,root,testmod,start");
    assertAll(
        () -> assertEquals("run", msg.getAction()),
        () -> assertEquals("https://example.com/mod.zip", msg.getUrl()),
        () -> assertEquals("root", msg.getType()),
        () -> assertEquals("testmod", msg.getModName()),
        () -> assertEquals("start", msg.getLaunchMap()));
  }

  @Test
  @DisplayName("gamedir level mod without a launch map")
  void test_gamedir_instantiation() {
    var msg = new InstallerArguments("q1package:https://example.com/mod.zip,gamedir,testmod");
    assertAll(
        () -> assertEquals("install", msg.getAction()),
        () -> assertEquals("https://example.com/mod.zip", msg.getUrl()),
        () -> assertEquals("gamedir", msg.getType()),
        () -> assertEquals("testmod", msg.getModName()),
        () -> assertNull(msg.getLaunchMap()));
  }

  @Test
  @DisplayName("gamedir level mod with a launch map")
  void test_gamedir_instantiation_with_start_map() {
    var msg = new InstallerArguments("q1package:https://example.com/mod.zip,gamedir,testmod,start");
    assertAll(
        () -> assertEquals("run", msg.getAction()),
        () -> assertEquals("https://example.com/mod.zip", msg.getUrl()),
        () -> assertEquals("gamedir", msg.getType()),
        () -> assertEquals("testmod", msg.getModName()),
        () -> assertEquals("start", msg.getLaunchMap()));
  }

  @Test
  @DisplayName("Map package without launch map")
  void test_map_instantiation() {
    var msg = new InstallerArguments("q1package:https://example.com/map.zip,map,testmod");
    assertAll(
        () -> assertEquals("install", msg.getAction()),
        () -> assertEquals("https://example.com/map.zip", msg.getUrl()),
        () -> assertEquals("map", msg.getType()),
        () -> assertEquals("testmod", msg.getModName()),
        () -> assertNull(msg.getLaunchMap()));
  }

  @Test
  @DisplayName("Map package with launch map")
  void test_map_instantiation_with_start_map() {
    var msg = new InstallerArguments("q1package:https://example.com/map.zip,map,testmod,start");
    assertAll(
        () -> assertEquals("run", msg.getAction()),
        () -> assertEquals("https://example.com/map.zip", msg.getUrl()),
        () -> assertEquals("map", msg.getType()),
        () -> assertEquals("testmod", msg.getModName()),
        () -> assertEquals("start", msg.getLaunchMap()));
  }

  @Test
  @DisplayName("Map package with dependency, no launch map")
  void test_mod_map_instantiation() {
    var msg = new InstallerArguments(
        "q1package:https://example.com/map.zip,mod-map,testmod,https://example.com/mod.zip|gamedir|testmod");
    assertAll(
        () -> assertEquals("install", msg.getAction()),
        () -> assertEquals("https://example.com/map.zip", msg.getUrl()),
        () -> assertEquals("mod-map", msg.getType()),
        () -> assertEquals("testmod", msg.getModName()),
        () -> assertEquals("install", msg.getModPackage().getAction()),
        () -> assertEquals("https://example.com/mod.zip", msg.getModPackage().getUrl()),
        () -> assertEquals("gamedir", msg.getModPackage().getType()),
        () -> assertEquals("testmod", msg.getModPackage().getModName()),
        () -> assertNull(msg.getLaunchMap()));
  }

  @Test
  @DisplayName("Map package with dependency and launch map")
  void test_mod_map_instantiation_with_start_map() {
    var msg = new InstallerArguments(
        "q1package:https://example.com/map.zip,mod-map,testmod,https://example.com/mod.zip|gamedir|testmod,start");
    assertAll(
        () -> assertEquals("run", msg.getAction()),
        () -> assertEquals("https://example.com/map.zip", msg.getUrl()),
        () -> assertEquals("mod-map", msg.getType()),
        () -> assertEquals("testmod", msg.getModName()),
        () -> assertEquals("install", msg.getModPackage().getAction()),
        () -> assertEquals("https://example.com/mod.zip", msg.getModPackage().getUrl()),
        () -> assertEquals("gamedir", msg.getModPackage().getType()),
        () -> assertEquals("testmod", msg.getModPackage().getModName()),
        () -> assertEquals("start", msg.getLaunchMap()));
  }

  @Test
  void test_custom_instantiation() {}
}
