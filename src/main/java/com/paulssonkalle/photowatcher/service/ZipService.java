package com.paulssonkalle.photowatcher.service;

import com.paulssonkalle.photowatcher.domain.YearMonth;
import com.paulssonkalle.photowatcher.domain.ZipFileDetail;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ZipService {
  private final PathService pathService;

  public ZipFileDetail zipFolder(String path) throws IOException {
    final YearMonth yearMonth = pathService.getYearMonth(path);
    final ZipFileDetail zipFileDetail = createZipFileDetail(yearMonth);
    log.info(
        "Starting to zip {} to {}", zipFileDetail.sourceFolder(), zipFileDetail.destinationFile());
    try (var directoryStream =
            Files.newDirectoryStream(zipFileDetail.sourceFolder(), Files::isRegularFile);
        var zos =
            new ZipOutputStream(
                new BufferedOutputStream(
                    Files.newOutputStream(
                        zipFileDetail.destinationFile(),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING)))) {

      for (Path sourceFile : directoryStream) {
        final var zipPath =
            Paths.get(yearMonth.year())
                .resolve(yearMonth.month())
                .resolve(sourceFile.getFileName());
        zos.setLevel(Deflater.NO_COMPRESSION);
        zos.putNextEntry(new ZipEntry(zipPath.toString()));
        Files.copy(sourceFile, zos);
        zos.closeEntry();
      }
    }
    log.info(
        "Successfully zipped {} to {}",
        zipFileDetail.sourceFolder(),
        zipFileDetail.destinationFile());
    return zipFileDetail;
  }

  private ZipFileDetail createZipFileDetail(YearMonth yearMonth) {
    final Path sourceFolder =
        pathService.getPhotosPath().resolve(yearMonth.year()).resolve(yearMonth.month());
    final Path destinationZip =
        pathService.getBackupPath().resolve(yearMonth.year() + "_" + yearMonth.month() + ".zip");
    return new ZipFileDetail(sourceFolder, destinationZip);
  }
}
