package io.github.jjelliott.q1installer.console;

import java.util.Scanner;

public interface ConsoleMenu {

  default String prompt(String message) {
    System.out.println(message);
    return getScanner().nextLine();
  }
  Scanner getScanner();
}
