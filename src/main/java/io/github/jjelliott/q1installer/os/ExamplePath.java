package io.github.jjelliott.q1installer.os;

import io.github.jjelliott.q1installer.config.Game;

public interface ExamplePath {

  String gameDir(Game game);

  String engine(Game game);
}
