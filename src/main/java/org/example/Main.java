package org.example;

import org.example.parsers.FileInputParser;
import org.example.parsers.InputParser;
import org.example.services.ConfigRepl;
import org.example.services.KeyStoreService;
import org.example.services.RsaRepl;
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
    try (Scanner sc = new Scanner(System.in)) {
      ConfigRepl configRepl = new ConfigRepl(sc);
      ConfigRepl.Config config = configRepl.readConfig();
      RSAUtils.setTransformation(config.transformation());
      KeyPair keys = new KeyStoreService().loadOrCreate(config.keySize());
      new RsaRepl(sc, new DirectoryFilePicker(sc)).run(keys);
    }
  }
}
