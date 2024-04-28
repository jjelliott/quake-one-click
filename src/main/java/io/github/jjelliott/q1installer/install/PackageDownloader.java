package io.github.jjelliott.q1installer.install;

import io.github.jjelliott.q1installer.InstallerArguments;

import java.io.IOException;

public interface PackageDownloader {
  String downloadFile(InstallerArguments installerArguments) throws IOException, InterruptedException;
}
