package io.github.jjelliott.q1installer.install;

import static io.github.jjelliott.q1installer.install.FileUtil.copyFolder;

import io.github.jjelliott.q1installer.InstallerArguments;
import io.github.jjelliott.q1installer.config.Game;
import io.github.jjelliott.q1installer.config.InstalledPackage;
import io.github.jjelliott.q1installer.config.UserProps;
import io.github.jjelliott.q1installer.os.ConfigLocation;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.apache.commons.io.FilenameUtils;

@Singleton
public class QuakeDirPackageInstaller implements PackageInstaller {

  private final UserProps userProps;
  private final List<InstalledPackage> installed;

  private final ConfigLocation configLocation;
  private final List<Extractor> extractors;

  public QuakeDirPackageInstaller(
      UserProps userProps,
      List<InstalledPackage> installed,
      ConfigLocation configLocation,
      List<Extractor> extractors) {
    this.userProps = userProps;
    this.installed = installed;
    this.configLocation = configLocation;
    this.extractors = extractors;
  }

  @Override
  public void installPackage(InstallerArguments installerArguments, String fileName)
      throws IOException {
    var type = installerArguments.getType();
    var modName = installerArguments.getModName();
    var game = installerArguments.getGame();
    extractors.stream()
        .filter(it -> it.handles(FilenameUtils.getExtension(fileName)))
        .findFirst()
        .orElseThrow()
        .extract(configLocation.getCacheDirFile(fileName));
    Files.createDirectories(quakeDirectoryPath(game, modName));
    if (installerArguments.getType().equals("map")) {
      Files.createDirectories(quakeDirectoryPath(game, modName + "/maps/"));
    }
    try (var fileStream = Files.list(
        Path.of(configLocation.getCacheDirFile(FilenameUtils.getBaseName(fileName) + "/")))) {
      for (Path packageFilePath : fileStream.toList()) {
        if (Files.isDirectory(packageFilePath)) {
          if (packageFilePath.getFileName().toString().toLowerCase().equals(modName)
              || type.equals("root")) {
            copyFolder(packageFilePath, quakeDirectoryPath(game, modName));
          } else {
            copyFolder(
                packageFilePath,
                quakeDirectoryPath(
                    game,
                    modName + "/" + packageFilePath.getFileName().toString().toLowerCase()));
          }
        } else {
          Files.copy(
              packageFilePath,
              quakeDirectoryPath(
                  game,
                  modName
                      + (type.contains("map") ? "/maps/" : "/")
                      + packageFilePath.getFileName().toString().toLowerCase()),
              StandardCopyOption.REPLACE_EXISTING);
        }
      }
    }

    Files.writeString(
        Path.of(configLocation.getInstalledList()),
        (!installed.isEmpty() ? "\n" : "") + installerArguments.getUrl(),
        StandardOpenOption.WRITE,
        StandardOpenOption.APPEND);
  }

  Path quakeDirectoryPath(Game game, String subPath) {
    return Path.of(userProps.getGameProps(game).getDirectoryPath() + "/" + subPath);
  }
}
