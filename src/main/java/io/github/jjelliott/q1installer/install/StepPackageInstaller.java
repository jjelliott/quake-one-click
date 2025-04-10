package io.github.jjelliott.q1installer.install;

import static io.github.jjelliott.q1installer.install.FileUtil.copyFolder;

import io.github.jjelliott.q1installer.Game;
import io.github.jjelliott.q1installer.InstallerArguments;
import io.github.jjelliott.q1installer.config.InstalledPackage;
import io.github.jjelliott.q1installer.config.UserProps;
import io.github.jjelliott.q1installer.install.custom.InstallStep;
import io.github.jjelliott.q1installer.os.ConfigLocation;
import io.micronaut.core.type.Argument;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.apache.commons.io.FilenameUtils;

//@Singleton
public class StepPackageInstaller implements PackageInstaller {

  private final UserProps userProps;
  private final List<InstalledPackage> installed;

  private final ConfigLocation configLocation;
  private final List<Extractor> extractors;
  private final ObjectMapper objectMapper;

  public StepPackageInstaller(UserProps userProps, List<InstalledPackage> installed,
      ConfigLocation configLocation, List<Extractor> extractors, ObjectMapper objectMapper) {
    this.userProps = userProps;
    this.installed = installed;
    this.configLocation = configLocation;
    this.extractors = extractors;
    this.objectMapper = objectMapper;
  }

  @Override
  public void installPackage(InstallerArguments installerArguments, String fileName)
      throws IOException {
    var type = installerArguments.getType();
    var modName = installerArguments.getModName();
    var game = installerArguments.getGame();
    extractors.stream()
        .filter(it -> it.handles(FilenameUtils.getExtension(fileName)))
        .findFirst().orElseThrow()
        .extract(configLocation.getCacheDirFile(fileName));
    Files.createDirectories(quakeDirectoryPath(game, modName));
    List<InstallStep> steps = objectMapper.readValue(installerArguments.getInstallStepJson(),
        Argument.listOf(InstallStep.class));
    System.out.println(steps);
    for (var step : steps) {
      var filePath = Path.of(configLocation.getCacheDirFile(FilenameUtils.getBaseName(fileName) + "/" + step.filePath()));
      switch (step.action()){
        case MOVE -> {

        }
        case COPY_TO_GAME_DIR -> {
          if (Files.isDirectory(filePath)) {
            copyFolder(filePath,  Path.of(userProps.getGameProps(game).getDirectoryPath().toLowerCase()));
          } else {
            Files.copy(filePath, Path.of(userProps.getGameProps(game).getDirectoryPath().toLowerCase()),
                StandardCopyOption.REPLACE_EXISTING);
          }
        }
        case COPY_TO_MOD_DIR -> {
          if (Files.isDirectory(filePath)) {
            copyFolder(filePath,  quakeDirectoryPath(game,
                (modName +  step.filePath()).toLowerCase()));
          } else {
            Files.copy(filePath, quakeDirectoryPath(game,
                    (modName +  step.filePath()).toLowerCase()),
                StandardCopyOption.REPLACE_EXISTING);
          }
        }
        case COPY_TO_MAP_DIR -> {
          if (Files.isDirectory(filePath)) {
            copyFolder(filePath,  quakeDirectoryPath(game,
                (modName + "/maps/" + step.filePath()).toLowerCase()));
          } else {
            Files.copy(filePath, quakeDirectoryPath(game,
                    (modName + "/maps/" + step.filePath()).toLowerCase()),
                StandardCopyOption.REPLACE_EXISTING);
          }
        }
      }
    }
    Files.writeString(Path.of(configLocation.getInstalledList()),
        (!installed.isEmpty() ? "\n" : "") + installerArguments.getUrl(), StandardOpenOption.WRITE,
        StandardOpenOption.APPEND);

  }
  Path quakeDirectoryPath(Game game, String subPath) {
    return Path.of(userProps.getGameProps(game).getDirectoryPath() + "/" + subPath);
  }
}
