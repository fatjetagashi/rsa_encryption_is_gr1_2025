package org.example.services;

import static org.example.utils.AppLogger.LOG;

import org.example.utils.RSAUtils;

import java.util.Scanner;

public class ConfigRepl {

  public record Config(int keySize, String transformation) {}

  private final Scanner sc;

  public ConfigRepl(Scanner sc) {
    this.sc = sc;
  }

  public Config readConfig() {
    LOG.info("=== RSA configuration ===");
    int keySize = askKeySize();
    String transformation = askTransformation();

    LOG.info("");
    LOG.info(String.format("Using RSA key size: %s bits", keySize));
    LOG.info(String.format("Using cipher transformation: %s" , transformation));
    LOG.info("");

    return new Config(keySize, transformation);
  }

  private int askKeySize() {
    while (true) {
      LOG.info("");
      LOG.info("Choose RSA key size:");
      LOG.info("  1) 1024 bits (fast, but weak – not recommended)");
      LOG.info("  2) 2048 bits (default, recommended)");
      LOG.info("  3) 4096 bits (stronger, but slower)");
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
          LOG.info("Unknown option, please choose 1, 2 or 3.");
      }
    }
  }

  private String askTransformation() {
    while (true) {
      LOG.info("");
      LOG.info("Choose RSA padding / transformation:");
      LOG.info("  1) RSA/ECB/PKCS1Padding (default)");
      LOG.info("  2) RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
      System.out.print("Option [1]: ");

      String line = sc.nextLine().trim();

      switch (line) {
        case "2":
          return "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
        case "", "1":
          return RSAUtils.DEFAULT_TRANSFORMATION;
        default:
          LOG.info("Unknown option, please choose 1 or 2.");
      }
    }
  }
}
