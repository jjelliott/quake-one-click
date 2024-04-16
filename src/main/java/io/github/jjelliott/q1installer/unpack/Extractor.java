package io.github.jjelliott.q1installer.unpack;

public interface Extractor {

  boolean handles(String extension);

  void extract(String path);
}
