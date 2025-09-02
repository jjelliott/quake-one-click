package io.github.jjelliott.q1installer

import io.github.jjelliott.q1installer.error.ExitCodeException
import io.github.jjelliott.q1installer.install.PackageDownloader
import io.github.jjelliott.q1installer.install.PackageInstaller
import io.github.jjelliott.q1installer.launch.GameLauncher
import spock.lang.Specification

class Q1InstallerSpec extends Specification {

  def "installer throws exit code 2 when package fails to download"() {
    given:
      def arg = new InstallerArguments("q1package:https://example.com/map.zip,map,id1");
      PackageDownloader packageDownloader = Mock()
      PackageInstaller packageInstaller = Mock()
      GameLauncher gameLauncher = Mock()
      Q1Installer installer = new Q1Installer([], packageDownloader, packageInstaller, gameLauncher)
    when:
      installer.run(arg)
    then:
      1 * packageDownloader.downloadFile(arg) >> { throw new IOException("fake") }
      def exception = thrown(ExitCodeException)
      exception.exitCode == 2
      exception.message == "Failed to install package"
  }

  def "installer throws exit code 3 when game fails to launch"() {
    given:
      def arg = new InstallerArguments("q1package:https://example.com/map.zip,map,id1,coolmap");
      PackageDownloader packageDownloader = Mock()
      PackageInstaller packageInstaller = Mock()
      GameLauncher gameLauncher = Mock()
      Q1Installer installer = new Q1Installer([], packageDownloader, packageInstaller, gameLauncher)
    when:
      installer.run(arg)
    then:
      1 * packageDownloader.downloadFile(arg) >> "map.zip"
      1 * packageInstaller.installPackage(arg, "map.zip")
      1 * gameLauncher.launchGame(arg) >> { throw new IOException("something happened, oh no") }
      def exception = thrown(ExitCodeException)
      exception.exitCode == 3
      exception.message == "Unable to launch game"
  }

  def "installer throws exit code 4 when dependency fails to download"() {
    given:
      def arg = new InstallerArguments("q1package:https://example.com/map.zip,map,id1");
      PackageDownloader packageDownloader = Mock()
      PackageInstaller packageInstaller = Mock()
      GameLauncher gameLauncher = Mock()
      Q1Installer installer = new Q1Installer([], packageDownloader, packageInstaller, gameLauncher)
    when:
      installer.run(arg)
    then:
      1 * packageDownloader.downloadFile(arg) >> { throw new IOException("fake") }
      def exception = thrown(ExitCodeException)
      exception.exitCode == 2
      exception.message == "Failed to install package"
  }

}
