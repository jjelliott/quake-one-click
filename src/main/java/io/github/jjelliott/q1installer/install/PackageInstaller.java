package io.github.jjelliott.q1installer.install;

import io.github.jjelliott.q1installer.InstallerArguments;
import java.io.IOException;

public interface PackageInstaller {

  void installPackage(InstallerArguments installerArguments, String fileName) throws IOException;
}
