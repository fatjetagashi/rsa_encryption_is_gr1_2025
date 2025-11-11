package org.example.parsers;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;

public final class InputParser {
  private final Scanner sc;
  public InputParser(Scanner sc) {
    this.sc = sc;
  }
  public String parse() {
    System.out.print("Enter text to encrypt: ");
    return sc.nextLine();
  }
}
