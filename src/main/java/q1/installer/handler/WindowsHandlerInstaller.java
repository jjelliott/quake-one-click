package q1.installer.handler;

import io.micronaut.context.annotation.Requirements;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

@Singleton
@Requires(os= Requires.Family.WINDOWS)
public class WindowsHandlerInstaller implements HandlerInstaller {
  @Override
  public void install() {
    System.out.println("this will eventually do registy stuff - how?");
  }
}
