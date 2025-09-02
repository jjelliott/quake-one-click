package io.github.jjelliott.q1installer.error;

@FunctionalInterface
public interface ExceptionRunnable extends Runnable {

  @Override
  default void run() {
    try {
      runThrows();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  void runThrows() throws Exception;
}
