package org.example.parsers;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class FileInputParser {
  private final Path path;
  private final Charset charset;

  public FileInputParser(Path path) {
    this(path, StandardCharsets.UTF_8);
  }

  public FileInputParser(Path path, Charset charset) {
    this.path = path;
    this.charset = charset;
  }

  public InputStream parseStream() throws IOException {
    return new BufferedInputStream(Files.newInputStream(path, StandardOpenOption.READ));
  }

  public String parseString() throws IOException {
    return Files.readString(path, charset);
  }
}

