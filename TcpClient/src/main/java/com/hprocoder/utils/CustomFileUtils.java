package com.hprocoder.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class CustomFileUtils {

  /**
   * A constructor to prevent instantiation of the utility class.
   */
  CustomFileUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Save file in disk.
   *
   * @param fileContent content file.
   * @param fileName filename.
   * @param directory directory where save the file.
   * @throws IOException throw exception
   */
  public static void saveFileInDisk(byte[] fileContent, String fileName, String directory) throws IOException {
    if (fileContent == null || fileContent.length == 0) {
      throw new IllegalArgumentException("File content cannot be null or empty.");
    }

    if (fileName == null || fileName.isEmpty()) {
      throw new IllegalArgumentException("File name cannot be null or empty.");
    }

    if (directory == null || directory.isEmpty()) {
      throw new IllegalArgumentException("Directory path cannot be null or empty.");
    }

// Ensure the directory ends with a file separator ("/" or "\" depending on OS)
    if (!directory.endsWith(File.separator)) {
      directory += File.separator;
    }

// Create directory if it doesn't exist
    Path dirPath = Paths.get(directory);
    if (!Files.exists(dirPath)) {
      Files.createDirectories(dirPath);
    }

// Write the file to disk
    Path filePath = Paths.get(directory, fileName);
    Files.write(filePath, fileContent);
  }
}
