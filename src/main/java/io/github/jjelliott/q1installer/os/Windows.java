package io.github.jjelliott.q1installer.os;

import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

@Singleton
@Requires(os= Requires.Family.WINDOWS)
public class Windows implements HandlerInstaller, ConfigLocation {
  @Override
  public void install() {
    System.out.println("this will eventually do registy stuff - how?");
  }

  @Override
  public String getConfig() {
    return System.getProperty("user.home") + "/AppData/Local/q1-installer";
  }
}
