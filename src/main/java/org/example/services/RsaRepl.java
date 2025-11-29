package org.example.services;


import static org.example.utils.AppLogger.LOG;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;
import org.example.parsers.FileInputParser;
import org.example.parsers.InputParser;
import org.example.utils.DirectoryFilePicker;
import org.example.utils.RSAUtils;

public class RsaRepl {

  private final Scanner sc;
  private final RsaActions actions;

  public RsaRepl(Scanner sc, DirectoryFilePicker picker) {
    this.sc = sc;
    this.actions = new RsaActions(sc, picker);
  }

  public void run(KeyPair keyPair) {
    try {
      boolean running = true;

      while (running) {
        printMenu();
        int choice = readChoice();
        switch (choice) {
          case 1 -> actions.encryptFromTerminal(keyPair);
          case 2 -> actions.encryptFromFile(keyPair);
          case 3 -> actions.decryptFromTerminal(keyPair);
          case 4 -> actions.decryptFromFile(keyPair);
          case 5 -> actions.encryptLargeFromTerminal(keyPair);
          case 6 -> actions.encryptLargeFromFile(keyPair);
          case 7 -> actions.decryptLargeFromTerminal(keyPair);
          case 8 -> actions.decryptLargeFromFile(keyPair);
          case 9 -> actions.encryptFromTerminalWithCustomPublicKey();
          case 10 -> actions.decryptFromTerminalWithCustomPrivateKey();
          case 0 -> {
            LOG.info("Exiting...");
            running = false;
          }
          default -> LOG.info("Invalid option. Please choose again.");
        }
      }
    } catch (IOException e) {
      System.err.println("I/O error: " + e.getMessage());
    } catch (RuntimeException e) {
      System.err.println("Error: " + e.getMessage());
    } finally {
      sc.close();
    }
  }

  private void printMenu() {
    LOG.info("");
    LOG.info("Choose an option:");
    LOG.info("  1) Encrypt text from terminal input (single block)");
    LOG.info("  2) Encrypt text from a file in data/input (single block)");
    LOG.info("  3) Decrypt ciphertext (Base64) from terminal (single block)");
    LOG.info("  4) Decrypt ciphertext from a file in data/output (single block)");
    LOG.info("  5) Encrypt LARGE text from terminal (chunked RSA)");
    LOG.info("  6) Encrypt LARGE text from file (chunked RSA)");
    LOG.info("  7) Decrypt LARGE ciphertext from terminal (chunked RSA)");
    LOG.info("  8) Decrypt LARGE ciphertext from file (chunked RSA)");
    LOG.info("  9) Encrypt from terminal with custom PUBLIC key");
    LOG.info("  10) Decrypt from terminal with custom PRIVATE key");
    LOG.info("  0) Exit");
    System.out.print("Option: ");
  }

  private int readChoice() {
    while (true) {
      String line = sc.nextLine().trim();
      try {
        return Integer.parseInt(line);
      } catch (NumberFormatException e) {
        System.out.print("Please enter a number (0-10): ");
      }
    }
  }
}
