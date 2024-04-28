package io.github.jjelliott.q1installer.config;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

import java.net.http.HttpClient;
import java.util.Scanner;

@Factory
public class BeanInitializer {

  @Singleton
  Scanner scanner(){
    return new Scanner(System.in);
  }

  @Singleton
  HttpClient client(){
    return HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .followRedirects(HttpClient.Redirect.NEVER)
        .build();
  }
}
