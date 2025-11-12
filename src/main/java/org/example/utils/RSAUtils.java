package org.example.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class RSAUtils {

  public static final int DEFAULT_KEY_SIZE = 2048;
  public static final String DEFAULT_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
  private static String transformation = DEFAULT_TRANSFORMATION;


  private RSAUtils() {
  }

  public static void setTransformation(String transformation) {
    if (transformation == null || transformation.isBlank()) {
      throw new IllegalArgumentException("Transformation must not be null or blank");
    }
    RSAUtils.transformation = transformation;
  }


  public static KeyPair generateKeyPair(int keySize) {
    try {
      KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
      kpg.initialize(keySize);
      return kpg.generateKeyPair();
    } catch (GeneralSecurityException e) {
      throw new RuntimeException("Failed to generate RSA key pair", e);
    }
  }

  public static String encryptToBase64(String plainText, PublicKey publicKey, Charset charset) {
    try {
      if (charset == null) {
        charset = StandardCharsets.UTF_8;
      }

      Cipher cipher = Cipher.getInstance(transformation);
      cipher.init(Cipher.ENCRYPT_MODE, publicKey);

      byte[] inputBytes = plainText.getBytes(charset);
      byte[] encrypted = cipher.doFinal(inputBytes);

      return Base64.getEncoder().encodeToString(encrypted);

    } catch (GeneralSecurityException e) {
      throw new RuntimeException("RSA encryption failed", e);
    }
  }

  public static String encryptToBase64(String plainText, PublicKey publicKey) {
    return encryptToBase64(plainText, publicKey, StandardCharsets.UTF_8);
  }

  public static String decryptFromBase64(String base64Ciphertext, PrivateKey privateKey,
      Charset charset) {
    try {
      if (charset == null) {
        charset = StandardCharsets.UTF_8;
      }

      byte[] ct = Base64.getDecoder().decode(base64Ciphertext);
      Cipher cipher = Cipher.getInstance(transformation);
      cipher.init(Cipher.DECRYPT_MODE, privateKey);
      byte[] pt = cipher.doFinal(ct);
      return new String(pt, charset);
    } catch (GeneralSecurityException | IllegalArgumentException e) {
      throw new RuntimeException(
          "RSA decryption failed (is the ciphertext Base64 and matching the transformation?)", e);
    }
  }

  public static String decryptFromBase64(String base64Ciphertext, PrivateKey privateKey) {
    return decryptFromBase64(base64Ciphertext, privateKey, StandardCharsets.UTF_8);
  }

  public static String encryptLargeToBase64(String plainText,
      PublicKey publicKey,
      Charset charset) {
    try {
      if (charset == null) {
        charset = StandardCharsets.UTF_8;
      }
      if (transformation == null || transformation.isBlank()) {
        transformation = DEFAULT_TRANSFORMATION;
      }

      byte[] input = plainText.getBytes(charset);

      Cipher cipher = Cipher.getInstance(transformation);
      cipher.init(Cipher.ENCRYPT_MODE, publicKey);

      int keySizeBytes = ((RSAPublicKey) publicKey).getModulus().bitLength() / 8;
      int maxPlainBlockSize = maxPlainBlockSize(keySizeBytes, transformation);

      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      for (int offset = 0; offset < input.length; offset += maxPlainBlockSize) {
        int len = Math.min(maxPlainBlockSize, input.length - offset);
        byte[] block = cipher.doFinal(input, offset, len);
        baos.write(block);
      }

      byte[] allEncrypted = baos.toByteArray();
      return Base64.getEncoder().encodeToString(allEncrypted);

    } catch (GeneralSecurityException e) {
      throw new RuntimeException("RSA chunked encryption failed", e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String encryptLargeToBase64(String plainText,
      PublicKey publicKey) {
    return encryptLargeToBase64(plainText, publicKey, StandardCharsets.UTF_8);
  }

  public static String decryptLargeFromBase64(String base64Ciphertext,
      PrivateKey privateKey,
      Charset charset) {
    try {
      if (charset == null) {
        charset = StandardCharsets.UTF_8;
      }
      if (transformation == null || transformation.isBlank()) {
        transformation = DEFAULT_TRANSFORMATION;
      }

      byte[] data = Base64.getDecoder().decode(base64Ciphertext);

      Cipher cipher = Cipher.getInstance(transformation);
      cipher.init(Cipher.DECRYPT_MODE, privateKey);

      int keySizeBytes = ((RSAPrivateKey) privateKey).getModulus().bitLength() / 8;

      if (data.length % keySizeBytes != 0) {
        throw new IllegalArgumentException(
            "Ciphertext length (" + data.length + ") is not a multiple of block size ("
                + keySizeBytes + ")"
        );
      }

      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      for (int offset = 0; offset < data.length; offset += keySizeBytes) {
        byte[] block = cipher.doFinal(data, offset, keySizeBytes);
        baos.write(block);
      }

      return baos.toString(charset);

    } catch (GeneralSecurityException | IllegalArgumentException | IOException e) {
      throw new RuntimeException("RSA chunked decryption failed", e);
    }
  }

  public static String decryptLargeFromBase64(String base64Ciphertext,
      PrivateKey privateKey) {
    return decryptLargeFromBase64(base64Ciphertext, privateKey, StandardCharsets.UTF_8);
  }


  public static PublicKey decodePublicKeyFromBase64(String base64) {
    try {
      byte[] bytes = Base64.getDecoder().decode(base64);
      X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
      KeyFactory kf = KeyFactory.getInstance("RSA");
      return kf.generatePublic(spec);
    } catch (GeneralSecurityException e) {
      throw new RuntimeException("Failed to decode public key from Base64", e);
    }
  }

  public static PrivateKey decodePrivateKeyFromBase64(String base64) {
    try {
      byte[] bytes = Base64.getDecoder().decode(base64);
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
      KeyFactory kf = KeyFactory.getInstance("RSA");
      return kf.generatePrivate(spec);
    } catch (GeneralSecurityException e) {
      throw new RuntimeException("Failed to decode private key from Base64", e);
    }
  }

  private static int maxPlainBlockSize(int keySizeBytes, String transformation) {
    String t = transformation.toUpperCase();

    if (t.contains("OAEPWITHSHA-256")) {
      int hLen = 32;
      return keySizeBytes - 2 * hLen - 2;
    } else if (t.contains("PKCS1PADDING")) {
      return keySizeBytes - 11;
    } else {
      return keySizeBytes - 11;
    }
  }

  public static void saveKeyToFile(Key key, Path path) throws IOException {
    Files.createDirectories(path.getParent());
    Files.write(path, key.getEncoded());
  }

  public static PublicKey loadPublicKey(Path path) {
    try {
      byte[] bytes = Files.readAllBytes(path);
      X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
      KeyFactory kf = KeyFactory.getInstance("RSA");
      return kf.generatePublic(spec);
    } catch (IOException | GeneralSecurityException e) {
      throw new RuntimeException("Failed to load public key from " + path, e);
    }
  }

  public static PrivateKey loadPrivateKey(Path path) {
    try {
      byte[] bytes = Files.readAllBytes(path);
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
      KeyFactory kf = KeyFactory.getInstance("RSA");
      return kf.generatePrivate(spec);
    } catch (IOException | GeneralSecurityException e) {
      throw new RuntimeException("Failed to load private key from " + path, e);
    }
  }
}
