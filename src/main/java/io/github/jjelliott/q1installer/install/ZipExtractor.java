package io.github.jjelliott.q1installer.install;

import io.github.jjelliott.q1installer.os.ConfigLocation;
import jakarta.inject.Singleton;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Singleton
public class ZipExtractor implements Extractor {

  private final ConfigLocation configLocation;

  public ZipExtractor(ConfigLocation configLocation) {
    this.configLocation = configLocation;
  }

  public boolean handles(String extension) {
    return extension.equalsIgnoreCase("zip");
  }

  public void extract(String filename) {
    var outputDir = configLocation.getCacheDirFile(FilenameUtils.getBaseName(filename));
    try (var inputStream = Files.newInputStream(Paths.get(filename))) {
      ArchiveStreamFactory archiveStreamFactory = new ArchiveStreamFactory();
      ArchiveInputStream<? extends ArchiveEntry> archiveInputStream = archiveStreamFactory.createArchiveInputStream(ArchiveStreamFactory.ZIP, inputStream);
      ArchiveEntry archiveEntry;
      while ((archiveEntry = archiveInputStream.getNextEntry()) != null) {
        Path path = Paths.get(outputDir, archiveEntry.getName());
        File file = path.toFile();
        if (archiveEntry.isDirectory()) {
          if (!file.isDirectory()) {
            file.mkdirs();
          }
        } else {
          File parent = file.getParentFile();
          if (!parent.isDirectory()) {
            parent.mkdirs();
          }
          try (OutputStream outputStream = Files.newOutputStream(path)) {
            IOUtils.copy(archiveInputStream, outputStream);
          }
        }
      }
    } catch (IOException | ArchiveException e) {
      throw new RuntimeException(e);
    }
  }
}
