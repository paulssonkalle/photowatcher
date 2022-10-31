package com.paulssonkalle.photowatcher.services;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ZipService {
  @Value("${app.paths.backup}") private Path backupPath;

  @Value("${app.paths.photos}") private Path photosPath;

  public void zipPhotoFolder(String year, String month) {
    final Path sourceFolder = photosPath.resolve(year).resolve(month);
    final Path destinationZip = backupPath.resolve(year + "_" + month + ".zip");
    log.info("Zipping {} to {}", sourceFolder, destinationZip);
    try (var directoryStream = Files.newDirectoryStream(sourceFolder, Files::isRegularFile);
        var zos =
            new ZipOutputStream(
                new BufferedOutputStream(
                    Files.newOutputStream(
                        destinationZip,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING)))) {

      directoryStream.forEach(
          sourceFile -> {
            final var zipPath = Paths.get(year).resolve(month).resolve(sourceFile.getFileName());
            try {
              zos.putNextEntry(new ZipEntry(zipPath.toString()));
              Files.copy(sourceFile, zos);
              zos.closeEntry();
            } catch (IOException e) {
              log.error("Failed to zip {} to {}", sourceFile, zipPath, e);
            }
          });
    } catch (IOException e) {
      log.error("Failed to zip for year {} and month {}", year, month, e);
    }
  }
}
