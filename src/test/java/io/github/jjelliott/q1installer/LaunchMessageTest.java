package io.github.jjelliott.q1installer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LaunchMessageTest {
  @Test
  void test_mod_instantiation() {
    var msg = new LaunchMessage("q1package:http://example.com/mod.zip,mod-folder,testmod");
    assertAll(
        () -> assertEquals("install", msg.action),
        () -> assertEquals("http://example.com/mod.zip", msg.url),
        () -> assertEquals("mod-folder", msg.type),
        () -> assertEquals("testmod", msg.modName),
        () -> assertNull(msg.launchMap)
    );
  }

  @Test
  void test_mod_instantiation_with_start_map() {
    var msg = new LaunchMessage("q1package:http://example.com/mod.zip,mod-folder,testmod,start");
    assertAll(
        () -> assertEquals("run", msg.action),
        () -> assertEquals("http://example.com/mod.zip", msg.url),
        () -> assertEquals("mod-folder", msg.type),
        () -> assertEquals("testmod", msg.modName),
        () -> assertEquals("start", msg.launchMap)
    );

  }

  @Test
  void test_gamedir_instantiation() {
    var msg = new LaunchMessage("q1package:http://example.com/mod.zip,gamedir,testmod");
    assertAll(
        () -> assertEquals("install", msg.action),
        () -> assertEquals("http://example.com/mod.zip", msg.url),
        () -> assertEquals("gamedir", msg.type),
        () -> assertEquals("testmod", msg.modName),
        () -> assertNull(msg.launchMap)
    );
  }

  @Test
  void test_gamedir_instantiation_with_start_map() {
    var msg = new LaunchMessage("q1package:http://example.com/mod.zip,gamedir,testmod,start");
    assertAll(
        () -> assertEquals("run", msg.action),
        () -> assertEquals("http://example.com/mod.zip", msg.url),
        () -> assertEquals("gamedir", msg.type),
        () -> assertEquals("testmod", msg.modName),
        () -> assertEquals("start", msg.launchMap)
    );

  }

  @Test
  void test_map_instantiation() {
    var msg = new LaunchMessage("q1package:http://example.com/map.zip,map,testmod");
    assertAll(
        () -> assertEquals("install", msg.action),
        () -> assertEquals("http://example.com/map.zip", msg.url),
        () -> assertEquals("map", msg.type),
        () -> assertEquals("testmod", msg.modName),
        () -> assertNull(msg.launchMap)
    );
  }

  @Test
  void test_map_instantiation_with_start_map() {
    var msg = new LaunchMessage("q1package:http://example.com/map.zip,map,testmod,start");
    assertAll(
        () -> assertEquals("run", msg.action),
        () -> assertEquals("http://example.com/map.zip", msg.url),
        () -> assertEquals("map", msg.type),
        () -> assertEquals("testmod", msg.modName),
        () -> assertEquals("start", msg.launchMap)
    );
  }

 @Test
  void test_mod_map_instantiation() {
    var msg = new LaunchMessage("q1package:http://example.com/map.zip,mod-map,testmod,http://example.com/mod.zip|gamedir|testmod");
    assertAll(
        () -> assertEquals("install", msg.action),
        () -> assertEquals("http://example.com/map.zip", msg.url),
        () -> assertEquals("mod-map", msg.type),
        () -> assertEquals("testmod", msg.modName),
        () -> assertEquals("install", msg.modPackage.action),
        () -> assertEquals("http://example.com/mod.zip", msg.modPackage.url),
        () -> assertEquals("gamedir", msg.modPackage.type),
        () -> assertEquals("testmod", msg.modPackage.modName),
        () -> assertNull(msg.launchMap)
    );
  }

  @Test
  void test_mod_map_instantiation_with_start_map() {
    var msg = new LaunchMessage("q1package:http://example.com/map.zip,mod-map,testmod,http://example.com/mod.zip|gamedir|testmod,start");
    assertAll(
        () -> assertEquals("run", msg.action),
        () -> assertEquals("http://example.com/map.zip", msg.url),
        () -> assertEquals("mod-map", msg.type),
        () -> assertEquals("testmod", msg.modName),
        () -> assertEquals("install", msg.modPackage.action),
        () -> assertEquals("http://example.com/mod.zip", msg.modPackage.url),
        () -> assertEquals("gamedir", msg.modPackage.type),
        () -> assertEquals("testmod", msg.modPackage.modName),
        () -> assertEquals("start", msg.launchMap)
    );
  }

  @Test
  void test_custom_instantiation() {

  }
}