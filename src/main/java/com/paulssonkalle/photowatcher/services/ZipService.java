package com.paulssonkalle.photowatcher.services;

import com.paulssonkalle.photowatcher.domain.YearMonth;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
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

  private final PhotoPathService photoPathService;

  public CompletableFuture<Void> zipFolder(String path) {
    final YearMonth yearMonth = photoPathService.getYearMonth(path);
    final Path sourceFolder = photosPath.resolve(yearMonth.year()).resolve(yearMonth.month());
    final Path destinationZip =
        backupPath.resolve(yearMonth.year() + "_" + yearMonth.month() + ".zip");
    log.info("Zipping {} to {}", sourceFolder, destinationZip);
    return CompletableFuture.runAsync(
        () -> {
          try (var directoryStream = Files.newDirectoryStream(sourceFolder, Files::isRegularFile);
              var zos =
                  new ZipOutputStream(
                      new BufferedOutputStream(
                          Files.newOutputStream(
                              destinationZip,
                              StandardOpenOption.CREATE,
                              StandardOpenOption.TRUNCATE_EXISTING)))) {

            for (Path sourceFile : directoryStream) {
              final var zipPath =
                  Paths.get(yearMonth.year())
                      .resolve(yearMonth.month())
                      .resolve(sourceFile.getFileName());
              zos.putNextEntry(new ZipEntry(zipPath.toString()));
              Files.copy(sourceFile, zos);
              zos.closeEntry();
            }
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
  }
}
