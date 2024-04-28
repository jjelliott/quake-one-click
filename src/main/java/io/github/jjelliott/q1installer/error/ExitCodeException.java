package io.github.jjelliott.q1installer.error;

public class ExitCodeException extends Exception {
  int exitCode;
  String message;

  public ExitCodeException(String message, Throwable cause, int exitCode) {
    super(message, cause);
    this.message = message;
    this.exitCode = exitCode;
  }

  public int getExitCode() {
    return exitCode;
  }

  @Override
  public String getMessage() {
    return message;
  }

  public static void doOrExit(ExceptionRunnable fn, String message, int code) throws ExitCodeException {
    try {
      fn.run();
    } catch (Exception e) {
      throw new ExitCodeException(message, e, code);
    }
  }
}
