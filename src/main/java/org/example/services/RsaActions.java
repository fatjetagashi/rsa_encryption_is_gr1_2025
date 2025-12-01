package org.example.services;

import static org.example.utils.AppLogger.LOG;

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

  private static final String NO_TEXT_PROVIDED = "No text provided.";
  private static final String ENCRYPTED_BASE64 = "\nEncrypted (Base64):";
  private static final String NO_FILE_SELECTED = "No file selected.";
  private static final String NO_CIPHERTEXT_PROVIDED = "No ciphertext provided.";
  private static final String DECRYPTED_PLAINTEXT = "\nDecrypted plaintext:";


  private final Scanner sc;
  private final DirectoryFilePicker picker;
  private final Path inputDir = Path.of("data","input");
  private final Path outputDir = Path.of("data","output");

  public RsaActions(Scanner sc, DirectoryFilePicker picker) {
    this.sc = sc; this.picker = picker;
  }
  public void encryptFromTerminal(KeyPair keyPair){
    LOG.info("");
    LOG.info("--- Encrypt from terminal (single block) ---");
    InputParser parser = new InputParser(sc);
    String plainText = parser.parse();
    if (plainText == null || plainText.isBlank()) {
      LOG.info(NO_TEXT_PROVIDED);
      return;
    }
    String cipherBase64 = RSAUtils.encryptToBase64(plainText, keyPair.getPublic());
    LOG.info(ENCRYPTED_BASE64);
    LOG.info(cipherBase64);
    String decrypted = RSAUtils.decryptFromBase64(cipherBase64, keyPair.getPrivate());
    LOG.info("\nDecrypted (for verification):");
    LOG.info(decrypted);
  }

  public void encryptFromFile(KeyPair keyPair) throws IOException{
    LOG.info("");
    LOG.info("--- Encrypt from file (single block) ---");
    if (!Files.isDirectory(inputDir)) {
      LOG.info(String.format("Input directory does not exist: %s" , inputDir.toAbsolutePath()));
      return;
    }
    Path chosen = picker.chooseFile(inputDir, "txt");
    if (chosen == null) {
      LOG.info(NO_FILE_SELECTED);
      return;
    }
    FileInputParser fileParser = new FileInputParser(chosen);
    String plainText = fileParser.parseString();
    LOG.info("\n--- Plaintext from file ---");
    LOG.info(plainText);
    String cipherBase64 = RSAUtils.encryptToBase64(plainText, keyPair.getPublic());
    LOG.info(ENCRYPTED_BASE64);
    LOG.info(cipherBase64);
    Files.createDirectories(outputDir);
    Path outFile = outputDir.resolve(chosen.getFileName().toString() + ".enc");
    Files.writeString(outFile, cipherBase64);
    LOG.info(String.format("\nEncrypted text was written to: %s" , outFile.toAbsolutePath()));
  }

  public void decryptFromTerminal(KeyPair keyPair) {
    LOG.info("");
    LOG.info("--- Decrypt from terminal (single block) ---");
    System.out.print("Paste Base64 ciphertext: ");
    String cipherBase64 = sc.nextLine().trim();
    if (cipherBase64.isEmpty()) {
      LOG.info(NO_CIPHERTEXT_PROVIDED);
      return;
    }
    try {
      String decrypted = RSAUtils.decryptFromBase64(cipherBase64, keyPair.getPrivate());
      LOG.info(DECRYPTED_PLAINTEXT);
      LOG.info(decrypted);
    } catch (RuntimeException e) {
      LOG.info(String.format("Failed to decrypt: %s" , e.getMessage()));
    }
  }

  public void decryptFromFile(KeyPair keyPair) throws IOException {
    LOG.info("");
    LOG.info("--- Decrypt from file (single block) ---");
    if (!Files.isDirectory(outputDir)) {
      LOG.info(String.format("Output directory does not exist: %s" , outputDir.toAbsolutePath()));
      return;
    }
    Path chosen = picker.chooseFile(outputDir, "enc");
    if (chosen == null) {
      LOG.info(NO_FILE_SELECTED);
      return;
    }
    String cipherBase64 = Files.readString(chosen);
    String decrypted = RSAUtils.decryptFromBase64(cipherBase64, keyPair.getPrivate());
    LOG.info(DECRYPTED_PLAINTEXT);
    LOG.info(decrypted);
  }

  public void decryptFromTerminalWithCustomPrivateKey() {
    LOG.info("");
    LOG.info("--- Decrypt from terminal with custom PRIVATE key ---");

    System.out.print("Paste Base64-encoded PRIVATE key: ");
    String privB64 = sc.nextLine().trim();
    if (privB64.isEmpty()) {
      LOG.info("No private key provided.");
      return;
    }

    System.out.print("Paste Base64 ciphertext: ");
    String cipherBase64 = sc.nextLine().trim();
    if (cipherBase64.isEmpty()) {
      LOG.info(NO_CIPHERTEXT_PROVIDED);
      return;
    }

    try {
      PrivateKey privateKey = RSAUtils.decodePrivateKeyFromBase64(privB64);
      String decrypted = RSAUtils.decryptFromBase64(cipherBase64, privateKey);
      LOG.info(DECRYPTED_PLAINTEXT);
      LOG.info(decrypted);
    } catch (RuntimeException e) {
      LOG.info(String.format("Failed to decrypt: %s" , e.getMessage()));
    }
  }

  public void encryptFromTerminalWithCustomPublicKey() {
    LOG.info("");
    LOG.info("--- Encrypt from terminal with custom PUBLIC key ---");

    System.out.print("Paste Base64-encoded PUBLIC key: ");
    String pubB64 = sc.nextLine().trim();
    if (pubB64.isEmpty()) {
      LOG.info("No public key provided.");
      return;
    }

    InputParser parser = new InputParser(sc);
    LOG.info("Enter plaintext to encrypt (end with empty line):");
    String plainText = parser.parse();
    if (plainText == null || plainText.isBlank()) {
      LOG.info(NO_TEXT_PROVIDED);
      return;
    }

    try {
      PublicKey publicKey = RSAUtils.decodePublicKeyFromBase64(pubB64);
      String cipherBase64 = RSAUtils.encryptToBase64(plainText, publicKey);
      LOG.info(ENCRYPTED_BASE64);
      LOG.info(cipherBase64);
    } catch (RuntimeException e) {
      LOG.info(String.format("Failed to encrypt: %s", e.getMessage()));
    }
  }

  public void encryptLargeFromTerminal(KeyPair keyPair){
    LOG.info("");
    LOG.info("--- Encrypt LARGE text from terminal (chunked RSA) ---");
    InputParser parser = new InputParser(sc);
    String plainText = parser.parse();
    if (plainText == null || plainText.isBlank()) {
      LOG.info(NO_TEXT_PROVIDED);
      return;
    }
    String cipherBase64 = RSAUtils.encryptLargeToBase64(plainText, keyPair.getPublic());
    LOG.info("\nEncrypted (Base64, chunked):");
    LOG.info(cipherBase64);

    // Optional verification
    String decrypted = RSAUtils.decryptLargeFromBase64(cipherBase64, keyPair.getPrivate());
    LOG.info("\nDecrypted (for verification, chunked):");
    LOG.info(decrypted);
  }

  public void encryptLargeFromFile(KeyPair keyPair) throws IOException{
    LOG.info("");
    LOG.info("--- Encrypt LARGE text from file (chunked RSA) ---");
    if (!Files.isDirectory(inputDir)) {
      LOG.info(String.format("Input directory does not exist: %s" , inputDir.toAbsolutePath()));
      return;
    }
    Path chosen = picker.chooseFile(inputDir, "txt");
    if (chosen == null) {
      LOG.info(NO_FILE_SELECTED);
      return;
    }
    FileInputParser fileParser = new FileInputParser(chosen);
    String plainText = fileParser.parseString();
    LOG.info("\n--- Plaintext from file ---");
    LOG.info(plainText);
    String cipherBase64 = RSAUtils.encryptLargeToBase64(plainText, keyPair.getPublic());
    LOG.info("\nEncrypted (Base64, chunked):");
    LOG.info(cipherBase64);
    Files.createDirectories(outputDir);
    Path outFile = outputDir.resolve(chosen.getFileName().toString() + ".large.enc");
    Files.writeString(outFile, cipherBase64);
    LOG.info(String.format("\nEncrypted text was written to: %s", outFile.toAbsolutePath()));
  }

  public void decryptLargeFromTerminal(KeyPair keyPair) {
    LOG.info("");
    LOG.info("--- Decrypt LARGE ciphertext from terminal (chunked RSA) ---");
    System.out.print("Paste Base64 ciphertext: ");
    String cipherBase64 = sc.nextLine().trim();
    if (cipherBase64.isEmpty()) {
      LOG.info(NO_CIPHERTEXT_PROVIDED);
      return;
    }
    try {
      String decrypted = RSAUtils.decryptLargeFromBase64(cipherBase64, keyPair.getPrivate());
      LOG.info("\nDecrypted plaintext (chunked):");
      LOG.info(decrypted);
    } catch (RuntimeException e) {
      LOG.info(String.format("Failed to decrypt: %s" , e.getMessage()));
    }
  }

  public void decryptLargeFromFile(KeyPair keyPair) throws IOException {
    LOG.info("");
    LOG.info("--- Decrypt LARGE ciphertext from file (chunked RSA) ---");
    if (!Files.isDirectory(outputDir)) {
      LOG.info(String.format("Output directory does not exist: %s" , outputDir.toAbsolutePath()));
      return;
    }
    Path chosen = picker.chooseFile(outputDir, "enc");
    if (chosen == null) {
      LOG.info(NO_FILE_SELECTED);
      return;
    }
    String cipherBase64 = Files.readString(chosen);
    String decrypted = RSAUtils.decryptLargeFromBase64(cipherBase64, keyPair.getPrivate());
    LOG.info("\nDecrypted plaintext (chunked):");
    LOG.info(decrypted);
  }
}
