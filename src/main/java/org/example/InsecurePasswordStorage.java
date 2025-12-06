package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class InsecurePasswordStorage {
  public static void main(String[] args) {
    String password = "MySecret123";

    try (FileWriter fw = new FileWriter("user_passwords.txt", true)) {
      fw.write("user: admin, password: " + password + "\n");
      System.out.println("Saved plaintext password to file! (insecure)");
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] hashed = md.digest(password.getBytes());
      System.out.println("Password hashed with MD5 (insecure): " + bytesToHex(hashed));
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }

  private static String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }
}