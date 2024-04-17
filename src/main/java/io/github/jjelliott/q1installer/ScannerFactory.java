package io.github.jjelliott.q1installer;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

import java.util.Scanner;

@Factory
public class ScannerFactory {
  @Singleton
  Scanner scanner(){
    return new Scanner(System.in);
  }
}
