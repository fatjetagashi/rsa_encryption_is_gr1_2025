package org.example.utils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Base64;

public final class RSAUtils {

    public static final int DEFAULT_KEY_SIZE = 1024;//2048
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

    public static String encryptToBase64(String plainText, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] inputBytes = plainText.getBytes(StandardCharsets.UTF_8);
            byte[] encrypted = cipher.doFinal(inputBytes);
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("RSA encryption failed", e);
        }
    }
}
