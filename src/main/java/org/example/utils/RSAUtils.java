package org.example.utils;

import java.nio.charset.Charset;
import java.security.PrivateKey;
import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Base64;

public final class RSAUtils {

    public static final int DEFAULT_KEY_SIZE = 2048;
    public static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    private RSAUtils() {
    }

    public static KeyPair generateKeyPair() {
        return generateKeyPair(DEFAULT_KEY_SIZE);
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

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
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

    public static String decryptFromBase64(String base64Ciphertext, PrivateKey privateKey, Charset charset) {
        try {
            if (charset == null) {
              charset = StandardCharsets.UTF_8;
            }

            byte[] ct = Base64.getDecoder().decode(base64Ciphertext);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] pt = cipher.doFinal(ct);
            return new String(pt, charset);
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new RuntimeException("RSA decryption failed (is the ciphertext Base64 and matching the transformation?)", e);
        }
    }

    public static String decryptFromBase64(String base64Ciphertext, PrivateKey privateKey) {
       return decryptFromBase64(base64Ciphertext, privateKey, StandardCharsets.UTF_8);
    }
}
