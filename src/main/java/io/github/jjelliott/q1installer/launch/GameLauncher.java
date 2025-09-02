package io.github.jjelliott.q1installer.launch;

import io.github.jjelliott.q1installer.InstallerArguments;
import java.io.IOException;

public interface GameLauncher {

  void launchGame(InstallerArguments installerArguments) throws IOException;
}
