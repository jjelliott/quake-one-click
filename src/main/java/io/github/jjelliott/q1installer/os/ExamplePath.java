package io.github.jjelliott.q1installer.os;

import io.github.jjelliott.q1installer.Game;
import io.github.jjelliott.q1installer.config.UserProps.GameProps;

public interface ExamplePath {

  String gameDir(Game game);

  String engine(Game game);
}
