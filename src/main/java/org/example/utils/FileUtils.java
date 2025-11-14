package org.example.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {

  public static Set<String> getExtensions(String[] extensions) {
    return Arrays.stream(extensions).filter(Objects::nonNull).map(s -> s.toLowerCase(Locale.ROOT))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public static List<Path> getFiles(Path dir, Set<String> exts) throws IOException {
    List<Path> files;
    try (Stream<Path> s = Files.list(dir)) {
      files = s.filter(Files::isRegularFile)
          .filter(p -> exts.isEmpty() || exts.contains(getFileExtension(p)))
          .sorted(Comparator.comparing(p -> p.getFileName().toString().toLowerCase(Locale.ROOT)))
          .toList();
    }

    return files;
  }

  public static String getFileExtension(Path p) {
    String name = p.getFileName().toString();
    int dot = name.lastIndexOf('.');
    return dot == -1 ? "" : name.substring(dot + 1).toLowerCase(Locale.ROOT);
  }

  public static String getFileSize(long b) {
    String[] u = {"B", "KB", "MB", "GB", "TB"};
    int i = 0;
    double x = b;
    while (x >= 1024 && i < u.length - 1) {
      x /= 1024;
      i++;
    }
    return String.format(Locale.ROOT, "%.1f %s", x, u[i]);
  }
}
