package io.github.jjelliott.q1installer;

public interface ProgressIndicator {

  void open();
  void update(int percentage);
  void close();

}
