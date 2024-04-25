package io.github.jjelliott.q1installer.os;

public interface ConfigLocation {

  String getConfig();

  default String getCacheDir(){
    return getConfig() + "/cache";
  }

  default String getCacheDirFile(String subPath){
    return getCacheDir() + "/" + subPath;
  }

  default String getUserPropertiesFile(){
    return getConfig() + "/user.properties";
  }

  default String getInstalledList(){
    return getConfig() + "/installed.list";
  }

  String getExampleQuakePath();
  String getExampleEnginePath();
}
