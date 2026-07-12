package io.github.jjelliott.q1installer.config;

import io.github.jjelliott.q1installer.ProgressIndicator;
import io.github.jjelliott.q1installer.console.ConsoleProgressIndicator;
import io.github.jjelliott.q1installer.gui.GuiProgressIndicator;
import io.micronaut.context.annotation.Factory;

@Factory
public class ProgressIndicatorConfig {

  private final UserProps userProps;


  public ProgressIndicatorConfig(UserProps userProps) {
    this.userProps = userProps;
  }

  public ProgressIndicator setupIndicator() {
    if(userProps.getMenuType().equals("console")){
      return new ConsoleProgressIndicator();
    }
    else if(userProps.getMenuType().equals("gui")){}
    return new GuiProgressIndicator();
  }
}
