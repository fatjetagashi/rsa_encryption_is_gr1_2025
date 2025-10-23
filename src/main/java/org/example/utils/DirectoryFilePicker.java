package org.example.utils;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DirectoryFilePicker {
  private final Scanner sc;

  /** Pass in your existing Scanner(System.in) so you control lifecycle. */

  public DirectoryFilePicker(Scanner sc) {
    this.sc = sc;
  }

  public Path chooseFile(Path dir, String... extensions) throws IOException {
    if (dir == null) dir = Path.of(".");
    if (!Files.isDirectory(dir)) throw new NoSuchFileException("Not a directory: " + dir);

    Set<String> exts = Arrays.stream(extensions)
        .filter(Objects::nonNull)
        .map(s -> s.toLowerCase(Locale.ROOT))
        .collect(Collectors.toCollection(LinkedHashSet::new));

    List<Path> files;
    try (Stream<Path> s = Files.list(dir)) {
      files = s.filter(Files::isRegularFile)
          .filter(p -> exts.isEmpty() || exts.contains(extOf(p)))
          .sorted(Comparator.comparing(p -> p.getFileName().toString().toLowerCase(Locale.ROOT)))
          .toList();
    }

    if (files.isEmpty()) {
      System.out.println("[Empty] No matching files in: " + dir.toAbsolutePath());
      return null;
    }

    System.out.println("Choose a file in: " + dir.toAbsolutePath());
    for (int i = 0; i < files.size(); i++) {
      Path p = files.get(i);
      long size = Files.size(p);
      System.out.printf(Locale.ROOT, "[%d] %s (%s)%n",
          i + 1, p.getFileName(), humanBytes(size));
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

  private static String extOf(Path p) {
    String name = p.getFileName().toString();
    int dot = name.lastIndexOf('.');
    return dot == -1 ? "" : name.substring(dot + 1).toLowerCase(Locale.ROOT);
  }

  private static String humanBytes(long b) {
    String[] u = {"B","KB","MB","GB","TB"};
    int i = 0;
    double x = b;
    while (x >= 1024 && i < u.length - 1) { x /= 1024; i++; }
    return String.format(Locale.ROOT, "%.1f %s", x, u[i]);
  }
}
