package org.example;

import org.example.parsers.FileInputParser;
import org.example.parsers.InputParser;
import org.example.utils.DirectoryFilePicker;
import org.example.utils.RSAUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;

public final class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.println("=== RSA Encryption Project ===");

            Path keyDir = Path.of("data", "keys");
            Files.createDirectories(keyDir);
            Path publicKeyPath = keyDir.resolve("public.key");
            Path privateKeyPath = keyDir.resolve("private.key");

            KeyPair keyPair;

            if (!Files.exists(publicKeyPath) || !Files.exists(privateKeyPath)) {
                System.out.println("No RSA keys found. Generating new key pair...");
                keyPair = RSAUtils.generateKeyPair();
                RSAUtils.saveKeyToFile(keyPair.getPublic(), publicKeyPath);
                RSAUtils.saveKeyToFile(keyPair.getPrivate(), privateKeyPath);
            } else {
                System.out.println("Loading RSA keys from disk...");
                PublicKey publicKey = RSAUtils.loadPublicKey(publicKeyPath);
                PrivateKey privateKey = RSAUtils.loadPrivateKey(privateKeyPath);
                keyPair = new KeyPair(publicKey, privateKey);
            }

            boolean running = true;

            while (running) {
                printMenu();
                int choice = readChoice(sc);
                switch (choice) {
                    case 1 -> encryptFromTerminal(sc, keyPair);
                    case 2 -> encryptFromFile(sc, keyPair);
                    case 3 -> decryptFromTerminal(sc, keyPair);
                    case 4 -> decryptFromFile(sc, keyPair);
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

    private static void printMenu() {
        System.out.println();
        System.out.println("Choose an option:");
        System.out.println("  1) Encrypt text from terminal input");
        System.out.println("  2) Encrypt text from a file in data/input");
        System.out.println("  3) Decrypt ciphertext (Base64) from terminal");
        System.out.println("  4) Decrypt ciphertext from a file in data/output");
        System.out.println("  0) Exit");
        System.out.print("Option: ");
    }

    private static int readChoice(Scanner sc) {
        while (true) {
            String line = sc.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a number (0-4): ");
            }
        }
    }

    private static void encryptFromTerminal(Scanner sc, KeyPair keyPair) {
        System.out.println();
        System.out.println("--- Encrypt from terminal ---");
        InputParser parser = new InputParser();
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

    private static void encryptFromFile(Scanner sc, KeyPair keyPair) throws IOException {
        System.out.println();
        System.out.println("--- Encrypt from file ---");
        DirectoryFilePicker picker = new DirectoryFilePicker(sc);
        Path inputDir = Path.of("data", "input");
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
        Path outputDir = Path.of("data", "output");
        Files.createDirectories(outputDir);
        Path outFile = outputDir.resolve(chosen.getFileName().toString() + ".enc");
        Files.writeString(outFile, cipherBase64);
        System.out.println("\nEncrypted text was written to: " + outFile.toAbsolutePath());
    }

    private static void decryptFromTerminal(Scanner sc, KeyPair keyPair) {
        System.out.println();
        System.out.println("--- Decrypt from terminal ---");
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

    private static void decryptFromFile(Scanner sc, KeyPair keyPair) throws IOException {
        System.out.println();
        System.out.println("--- Decrypt from file ---");
        DirectoryFilePicker picker = new DirectoryFilePicker(sc);
        Path outputDir = Path.of("data", "output");
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
}
