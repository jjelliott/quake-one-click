package io.github.jjelliott.q1installer.console;

import io.github.jjelliott.q1installer.ProgressIndicator;

public class ConsoleProgressIndicator implements ProgressIndicator {

  @Override
  public void open() {
    System.out.print("000%");
  }

  @Override
  public void update(int percentage) {
    System.out.print("\r" + percentage + "%");
  }

  @Override
  public void close() {
    System.out.println("\rDownload complete");
  }
}
