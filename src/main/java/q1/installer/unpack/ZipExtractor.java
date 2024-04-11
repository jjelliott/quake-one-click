package q1.installer.unpack;

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

import static q1.installer.Q1InstallerCommand.confDirPath;

@Singleton
public class ZipExtractor implements Extractor {

  public boolean handles(String extension) {
    return extension.equalsIgnoreCase("zip");
  }

  public void extract(String filename) {
    System.out.println("extracting " + filename);
    var outputDir = confDirPath + "/cache/" + FilenameUtils.getBaseName(filename);
    System.out.println("output dir: " + outputDir);
    try (var inputStream = Files.newInputStream(Paths.get(filename))) {
      ArchiveStreamFactory archiveStreamFactory = new ArchiveStreamFactory();
      ArchiveInputStream archiveInputStream = archiveStreamFactory.createArchiveInputStream(ArchiveStreamFactory.ZIP, inputStream);
      ArchiveEntry archiveEntry = null;
      while ((archiveEntry = archiveInputStream.getNextEntry()) != null){
        Path path = Paths.get(outputDir, archiveEntry.getName());
        File file = path.toFile();
        if (archiveEntry.isDirectory()){
          if (!file.isDirectory()){
            file.mkdirs();
          }
        } else {
          File parent = file.getParentFile();
          if (!parent.isDirectory()){
            parent.mkdirs();
          }
          try (OutputStream outputStream = Files.newOutputStream(path)){
            IOUtils.copy(archiveInputStream, outputStream);
          }
        }
      }
    } catch (IOException | ArchiveException e) {
      throw new RuntimeException(e);
    }
  }
}
