package org.example;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class KeyGeneratorScript{

  public static void main(String[] args) throws Exception {

    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);
    KeyPair keyPair = kpg.generateKeyPair();

    String publicKeyBase64 = Base64.getEncoder()
            .encodeToString(keyPair.getPublic().getEncoded());

    String privateKeyBase64 = Base64.getEncoder()
            .encodeToString(keyPair.getPrivate().getEncoded());

    System.out.println("Public key (Base64, 2048-bit):");
    System.out.println(publicKeyBase64);
    System.out.println();
    System.out.println("Private key (Base64, 2048-bit):");
    System.out.println(privateKeyBase64);
  }
}
