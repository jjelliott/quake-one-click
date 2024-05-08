package io.github.jjelliott.q1installer

import io.github.jjelliott.q1installer.config.UserProps
import spock.lang.Specification
import spock.lang.Unroll

class ProcessBuilderGameLauncherSpec extends Specification {
  @Unroll
  def "GenerateLaunchCommand creates expected launch command from #installer"() {
    given:
      def userProps = new UserProps("./test", "./test/game")
      def launcher = new ProcessBuilderGameLauncher(userProps)
    when:
      def output = launcher.generateLaunchCommand(installer)
    then:
      output.join(" ") == expected
    where:
      installer                                                                            | expected
      new InstallerArguments("q1package:https://example.com/test.zip,map,id1,testmap")     | "./test/game -basedir ./test +map testmap"
      new InstallerArguments("q1package:https://example.com/test.zip,root,mod,testmap")    | "./test/game -basedir ./test -game mod +map testmap"
      new InstallerArguments("q1package:https://example.com/test.zip,gamedir,mod,testmap") | "./test/game -basedir ./test -game mod +map testmap"
  }
}
