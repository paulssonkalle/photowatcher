package com.paulssonkalle.photowatcher.domain;

import java.nio.file.Path;

public record ZipFileDetail(Path sourceFolder, Path destinationFile) {
  public String getZipFilename() {
    return destinationFile.getFileName().toString();
  }
}
