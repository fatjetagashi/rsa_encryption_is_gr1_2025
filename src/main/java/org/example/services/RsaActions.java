package org.example.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;
import org.example.parsers.FileInputParser;
import org.example.parsers.InputParser;
import org.example.utils.DirectoryFilePicker;
import org.example.utils.RSAUtils;

public class RsaActions {
  private final Scanner sc;
  private final DirectoryFilePicker picker;
  private final Path inputDir = Path.of("data","input");
  private final Path outputDir = Path.of("data","output");

  public RsaActions(Scanner sc, DirectoryFilePicker picker) {
    this.sc = sc; this.picker = picker;
  }
  public void encryptFromTerminal(KeyPair keyPair){
    System.out.println();
    System.out.println("--- Encrypt from terminal (single block) ---");
    InputParser parser = new InputParser(sc);
    String plainText = parser.parse();
    if (plainText == null || plainText.isBlank()) {
      System.out.println("No text provided.");
      return;
    }
    String cipherBase64 = RSAUtils.encryptToBase64(plainText, keyPair.getPublic());
    System.out.println("\nEncrypted (Base64):");
    System.out.println(cipherBase64);
    String decrypted = RSAUtils.decryptFromBase64(cipherBase64, keyPair.getPrivate());
    System.out.println("\nDecrypted (for verification):");
    System.out.println(decrypted);
  }

  public void encryptFromFile(KeyPair keyPair) throws IOException{
    System.out.println();
    System.out.println("--- Encrypt from file (single block) ---");
    if (!Files.isDirectory(inputDir)) {
      System.out.println("Input directory does not exist: " + inputDir.toAbsolutePath());
      return;
    }
    Path chosen = picker.chooseFile(inputDir, "txt");
    if (chosen == null) {
      System.out.println("No file selected.");
      return;
    }
    FileInputParser fileParser = new FileInputParser(chosen);
    String plainText = fileParser.parseString();
    System.out.println("\n--- Plaintext from file ---");
    System.out.println(plainText);
    String cipherBase64 = RSAUtils.encryptToBase64(plainText, keyPair.getPublic());
    System.out.println("\nEncrypted (Base64):");
    System.out.println(cipherBase64);
    Files.createDirectories(outputDir);
    Path outFile = outputDir.resolve(chosen.getFileName().toString() + ".enc");
    Files.writeString(outFile, cipherBase64);
    System.out.println("\nEncrypted text was written to: " + outFile.toAbsolutePath());
  }

  public void decryptFromTerminal(KeyPair keyPair) {
    System.out.println();
    System.out.println("--- Decrypt from terminal (single block) ---");
    System.out.print("Paste Base64 ciphertext: ");
    String cipherBase64 = sc.nextLine().trim();
    if (cipherBase64.isEmpty()) {
      System.out.println("No ciphertext provided.");
      return;
    }
    try {
      String decrypted = RSAUtils.decryptFromBase64(cipherBase64, keyPair.getPrivate());
      System.out.println("\nDecrypted plaintext:");
      System.out.println(decrypted);
    } catch (RuntimeException e) {
      System.out.println("Failed to decrypt: " + e.getMessage());
    }
  }

  public void decryptFromFile(KeyPair keyPair) throws IOException {
    System.out.println();
    System.out.println("--- Decrypt from file (single block) ---");
    if (!Files.isDirectory(outputDir)) {
      System.out.println("Output directory does not exist: " + outputDir.toAbsolutePath());
      return;
    }
    Path chosen = picker.chooseFile(outputDir, "enc");
    if (chosen == null) {
      System.out.println("No file selected.");
      return;
    }
    String cipherBase64 = Files.readString(chosen);
    String decrypted = RSAUtils.decryptFromBase64(cipherBase64, keyPair.getPrivate());
    System.out.println("\nDecrypted plaintext:");
    System.out.println(decrypted);
  }

  public void decryptFromTerminalWithCustomPrivateKey() {
    System.out.println();
    System.out.println("--- Decrypt from terminal with custom PRIVATE key ---");

    System.out.print("Paste Base64-encoded PRIVATE key: ");
    String privB64 = sc.nextLine().trim();
    if (privB64.isEmpty()) {
      System.out.println("No private key provided.");
      return;
    }

    System.out.print("Paste Base64 ciphertext: ");
    String cipherBase64 = sc.nextLine().trim();
    if (cipherBase64.isEmpty()) {
      System.out.println("No ciphertext provided.");
      return;
    }

    try {
      PrivateKey privateKey = RSAUtils.decodePrivateKeyFromBase64(privB64);
      String decrypted = RSAUtils.decryptFromBase64(cipherBase64, privateKey);
      System.out.println("\nDecrypted plaintext:");
      System.out.println(decrypted);
    } catch (RuntimeException e) {
      System.out.println("Failed to decrypt: " + e.getMessage());
    }
  }

  public void encryptFromTerminalWithCustomPublicKey() {
    System.out.println();
    System.out.println("--- Encrypt from terminal with custom PUBLIC key ---");

    System.out.print("Paste Base64-encoded PUBLIC key: ");
    String pubB64 = sc.nextLine().trim();
    if (pubB64.isEmpty()) {
      System.out.println("No public key provided.");
      return;
    }

    InputParser parser = new InputParser(sc);
    System.out.println("Enter plaintext to encrypt (end with empty line):");
    String plainText = parser.parse();
    if (plainText == null || plainText.isBlank()) {
      System.out.println("No text provided.");
      return;
    }

    try {
      PublicKey publicKey = RSAUtils.decodePublicKeyFromBase64(pubB64);
      String cipherBase64 = RSAUtils.encryptToBase64(plainText, publicKey);
      System.out.println("\nEncrypted (Base64):");
      System.out.println(cipherBase64);
    } catch (RuntimeException e) {
      System.out.println("Failed to encrypt: " + e.getMessage());
    }
  }


}
