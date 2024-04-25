package io.github.jjelliott.q1installer;

import io.github.jjelliott.q1installer.os.ConfigLocation;
import io.github.jjelliott.q1installer.unpack.Extractor;
import jakarta.inject.Singleton;

import java.net.http.HttpClient;
import java.util.List;
import java.util.Scanner;

@Singleton
public class Q1InstallerFactory {
  private final Scanner scanner;
  private final HttpClient client;
  private final ConfigLocation configLocation;
  private final List<Extractor> extractors;
  private final UserProps userProps;
  private final List<InstalledPackage > installed;

  public Q1InstallerFactory(Scanner scanner, HttpClient client, ConfigLocation configLocation, List<Extractor> extractors, UserProps userProps, List<InstalledPackage> installed) {
    this.scanner = scanner;
    this.client = client;
    this.configLocation = configLocation;
    this.extractors = extractors;
    this.userProps = userProps;
    this.installed = installed;
  }

  Q1Installer get(String message) {
    return new Q1Installer(userProps, installed, scanner, client, configLocation, extractors, message);
  }
}
