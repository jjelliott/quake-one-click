package io.github.jjelliott.q1installer.install;

import io.github.jjelliott.q1installer.InstallerArguments;
import io.github.jjelliott.q1installer.os.ConfigLocation;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Singleton
public class JavaHttpClientPackageDownloader implements PackageDownloader {

  private final HttpClient client;
  private final ConfigLocation configLocation;

  public JavaHttpClientPackageDownloader(HttpClient client, ConfigLocation configLocation) {
    this.client = client;
    this.configLocation = configLocation;
  }

  @Override
  public String downloadFile(InstallerArguments installerArguments)
      throws IOException, InterruptedException {
    String fileName = "";
    if (installerArguments.getUrl().endsWith(".zip")) {
      var urlSplit = installerArguments.getUrl().split("/");
      fileName = urlSplit[urlSplit.length - 1];
    }

    HttpResponse<byte[]> response;

    response = client.send(
        HttpRequest.newBuilder(URI.create(installerArguments.getUrl())).build(),
        HttpResponse.BodyHandlers.ofByteArray());

    if (response.statusCode() >= 300 && response.statusCode() < 400) {
      if (!response.headers().allValues("location").isEmpty()) {
        installerArguments.setUrl(response.headers().firstValue("location").orElseThrow());
        return downloadFile(installerArguments);
      }
    }
    if (fileName.isEmpty()) {
      var disposition = response.headers().firstValue("content-disposition").orElseThrow();
      if (disposition.contains("attachment; filename=\"")) {
        fileName = disposition.replace("attachment; filename=\"", "").replace("\"", "");
      }
    }
    Files.write(
        Path.of(configLocation.getCacheDirFile(fileName)),
        response.body(),
        StandardOpenOption.CREATE,
        StandardOpenOption.WRITE);

    return fileName;
  }
}
