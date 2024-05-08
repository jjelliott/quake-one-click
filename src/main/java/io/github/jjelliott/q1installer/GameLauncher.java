package io.github.jjelliott.q1installer;

import java.io.IOException;

public interface GameLauncher {
  void launchGame(InstallerArguments installerArguments) throws IOException;
}
