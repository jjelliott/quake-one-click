package io.github.jjelliott.q1installer.install;

public interface Extractor {

  boolean handles(String extension);

  void extract(String path);
}
