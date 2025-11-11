package org.example.services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.example.utils.RSAUtils;

public class KeyStoreService {

  private final Path publicKeyPath;
  private final Path privateKeyPath;
  private final Path keyDir;

  public KeyStoreService() {
    this.keyDir = Path.of("data", "keys");
    this.publicKeyPath = keyDir.resolve("public.key");
    this.privateKeyPath = keyDir.resolve("private.key");
  }

  public KeyPair loadOrCreate(int keySize){
    try {

      Files.createDirectories(keyDir);
      KeyPair keyPair;

      if (!Files.exists(publicKeyPath) || !Files.exists(privateKeyPath)) {
        System.out.println("No RSA keys found. Generating new key pair...");
        keyPair = RSAUtils.generateKeyPair(keySize);
        RSAUtils.saveKeyToFile(keyPair.getPublic(), publicKeyPath);
        RSAUtils.saveKeyToFile(keyPair.getPrivate(), privateKeyPath);
        return keyPair;
      }
      System.out.println("Loading RSA keys from disk...");
      PublicKey publicKey = RSAUtils.loadPublicKey(publicKeyPath);
      PrivateKey privateKey = RSAUtils.loadPrivateKey(privateKeyPath);
      return new KeyPair(publicKey, privateKey);

    } catch (Exception e) {
      throw new RuntimeException("Key load/create failed", e);
    }
  }

  public KeyPair loadOrCreate() {
    return loadOrCreate(RSAUtils.DEFAULT_KEY_SIZE);
  }
}
