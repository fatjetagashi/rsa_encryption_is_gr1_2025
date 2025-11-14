package org.example.utils;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DirectoryFilePicker {
  private final Scanner sc;

  public DirectoryFilePicker(Scanner sc) {
    this.sc = sc;
  }

  public Path chooseFile(Path dir, String... extensions) throws IOException {
    if (dir == null) dir = Path.of(".");
    if (!Files.isDirectory(dir)) throw new NoSuchFileException("Not a directory: " + dir);

    Set<String> exts = FileUtils.getExtensions(extensions);

    List<Path> files = FileUtils.getFiles(dir, exts);

    if (files.isEmpty()) {
      System.out.println("[Empty] No matching files in: " + dir.toAbsolutePath());
      return null;
    }

    System.out.println("Choose a file in: " + dir.toAbsolutePath());
    for (int i = 0; i < files.size(); i++) {
      Path p = files.get(i);
      long size = Files.size(p);
      System.out.printf(Locale.ROOT, "[%d] %s (%s)%n",
          i + 1, p.getFileName(), FileUtils.getFileSize(size));
    }
    System.out.println("[0] Cancel");

    while (true) {
      System.out.print("Enter number: ");
      String line = sc.nextLine().trim();
      int idx;
      try { idx = Integer.parseInt(line); }
      catch (NumberFormatException e) { System.out.println("Not a number. Try again."); continue; }

      if (idx == 0) return null;
      if (1 <= idx && idx <= files.size()) return files.get(idx - 1);
      System.out.println("Out of range. Try again.");
    }
  }
}
