package org.example;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;
import org.example.utils.DirectoryFilePicker;

public class MainTest {
  public static void main(String[] args) throws IOException {
    Scanner sc = new Scanner(System.in);
    DirectoryFilePicker chooser = new DirectoryFilePicker(sc);
    Path chosen = chooser.chooseFile(null);
  }

}
