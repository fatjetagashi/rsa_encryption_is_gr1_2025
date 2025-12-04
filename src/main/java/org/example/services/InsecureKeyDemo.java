package org.example.services;

public class InsecureKeyDemo {
  private static final String DEFAULT_PRIVATE_KEY_PASSWORD = "SuperSecretPassword123!";
  public String getDefaultPassword() {
    return DEFAULT_PRIVATE_KEY_PASSWORD;
  }
}
