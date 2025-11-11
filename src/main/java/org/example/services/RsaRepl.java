package org.example.services;


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
          case 9 -> actions.encryptFromTerminalWithCustomPublicKey();
          case 10 -> actions.decryptFromTerminalWithCustomPrivateKey();
          case 0 -> {
            System.out.println("Exiting...");
            running = false;
          }
          default -> System.out.println("Invalid option. Please choose again.");
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
    System.out.println();
    System.out.println("Choose an option:");
    System.out.println("  1) Encrypt text from terminal input (single block)");
    System.out.println("  2) Encrypt text from a file in data/input (single block)");
    System.out.println("  3) Decrypt ciphertext (Base64) from terminal (single block)");
    System.out.println("  4) Decrypt ciphertext from a file in data/output (single block)");
    System.out.println("  9) Encrypt from terminal with custom PUBLIC key");
    System.out.println("  10) Decrypt from terminal with custom PRIVATE key");
    System.out.println("  0) Exit");
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
