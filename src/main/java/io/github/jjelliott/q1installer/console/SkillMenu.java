package io.github.jjelliott.q1installer.console;

import io.github.jjelliott.q1installer.config.UserProps;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Scanner;

@Singleton
public class SkillMenu implements ConsoleMenu {

  private static final List<String> VALID_INPUTS = List.of("0", "1", "2", "3", "a");

  private final UserProps userProps;
  private final Scanner scanner;

  public SkillMenu(UserProps userProps, Scanner scanner) {
    this.userProps = userProps;
    this.scanner = scanner;
  }

  void show() {
    var input = prompt(
        """
        This is the skill that the app will launch by default.
        Please input just the number:
        0 - Easy
        1 - Normal
        2 - Hard
        3 - Nightmare
        a - Ask every time
        """);
    if (VALID_INPUTS.contains(input)) {
      if (input.equals("a")) {
        userProps.setSkill(-1);
      } else {
        userProps.setSkill(Integer.parseInt(input));
      }
    } else {
      System.out.println("Invalid input, please try again.");
      show();
    }
  }

  @Override
  public Scanner getScanner() {
    return scanner;
  }
}
