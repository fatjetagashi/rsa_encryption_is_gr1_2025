package org.example.services;

import org.example.utils.RSAUtils;

import java.util.Scanner;

public class ConfigRepl {

  public record Config(int keySize, String transformation) {}

  private final Scanner sc;

  public ConfigRepl(Scanner sc) {
    this.sc = sc;
  }

  public Config readConfig() {
    System.out.println("=== RSA configuration ===");
    int keySize = askKeySize();
    String transformation = askTransformation();

    System.out.println();
    System.out.println("Using RSA key size: " + keySize + " bits");
    System.out.println("Using cipher transformation: " + transformation);
    System.out.println();

    return new Config(keySize, transformation);
  }

  private int askKeySize() {
    while (true) {
      System.out.println();
      System.out.println("Choose RSA key size:");
      System.out.println("  1) 1024 bits (fast, but weak – not recommended)");
      System.out.println("  2) 2048 bits (default, recommended)");
      System.out.println("  3) 4096 bits (stronger, but slower)");
      System.out.print("Option [2]: ");

      String line = sc.nextLine().trim();

      switch (line) {
        case "1":
          return 1024;
        case "3":
          return 4096;
        case "", "2":
          return RSAUtils.DEFAULT_KEY_SIZE;
        default:
          System.out.println("Unknown option, please choose 1, 2 or 3.");
      }
    }
  }

  private String askTransformation() {
    while (true) {
      System.out.println();
      System.out.println("Choose RSA padding / transformation:");
      System.out.println("  1) RSA/ECB/PKCS1Padding (default)");
      System.out.println("  2) RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
      System.out.print("Option [1]: ");

      String line = sc.nextLine().trim();

      switch (line) {
        case "2":
          return "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
        case "", "1":
          return RSAUtils.DEFAULT_TRANSFORMATION;
        default:
          System.out.println("Unknown option, please choose 1 or 2.");
      }
    }
  }
}
