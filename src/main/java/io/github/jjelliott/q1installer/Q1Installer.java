package io.github.jjelliott.q1installer;

import static io.github.jjelliott.q1installer.error.ExitCodeException.doOrExit;

import io.github.jjelliott.q1installer.config.InstalledPackage;
import io.github.jjelliott.q1installer.error.ExitCodeException;
import io.github.jjelliott.q1installer.install.PackageDownloader;
import io.github.jjelliott.q1installer.install.PackageInstaller;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class Q1Installer {

  private final List<InstalledPackage> installed;
  private final PackageDownloader packageDownloader;
  private final PackageInstaller packageInstaller;
  private final GameLauncher gameLauncher;

  public Q1Installer(List<InstalledPackage> installed,
      PackageDownloader packageDownloader,
      PackageInstaller packageInstaller,
      GameLauncher gameLauncher) {
    this.installed = installed;
    this.packageDownloader = packageDownloader;
    this.packageInstaller = packageInstaller;
    this.gameLauncher = gameLauncher;
  }


  public void run(InstallerArguments installerArguments) throws ExitCodeException {

    var modPackage = installerArguments.getModPackage();

    if (modPackage != null) {
      if (isNotInstalled(modPackage)) {
        downloadAndInstall(modPackage, "Failed to install parent package", 4);
      } else {
        System.out.println("Skipping dependency install step because already installed");
      }
    }
    if (isNotInstalled(installerArguments)) {
      downloadAndInstall(installerArguments, "Failed to install package", 2);
    } else {
      System.out.println("Skipping install step because already installed");
    }
    if (installerArguments.getAction().equals("run")) {
      doOrExit(() -> gameLauncher.launchGame(installerArguments), "Unable to launch game", 3);
    }

  }

  boolean isNotInstalled(InstallerArguments installerArguments) {
    return installed.stream()
        .noneMatch(
            installedPackage -> installedPackage.sourceUrl().equals(installerArguments.getUrl()));
  }

  void downloadAndInstall(InstallerArguments installerArguments, String errorMessage, int exitCode)
      throws ExitCodeException {
    doOrExit(() -> {
      var filename = packageDownloader.downloadFile(installerArguments);
      packageInstaller.installPackage(installerArguments, filename);
    }, errorMessage, exitCode);
  }

}

