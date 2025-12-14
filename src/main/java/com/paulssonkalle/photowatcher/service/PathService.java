package com.paulssonkalle.photowatcher.service;

import com.paulssonkalle.photowatcher.config.properties.PathProperties;
import com.paulssonkalle.photowatcher.domain.YearMonth;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
public class PathService {
  private static final Pattern yearMonthPattern = Pattern.compile("\\d{4}/(0[1-9]|1[0-2])");
  @Getter private final Path backupPath;
  @Getter private final Path photosPath;
  @Getter private final Path downloadPath;

  public PathService(PathProperties pathProperties) {
    this.backupPath = Path.of(pathProperties.backup());
    this.photosPath = Path.of(pathProperties.photos());
    this.downloadPath = Path.of(pathProperties.download());
  }

  public YearMonth getYearMonth(String path) {
    verifyPhotoFolder(path);
    String[] paths = path.split(File.separator);
    return new YearMonth(paths[0], paths[1]);
  }

  public Path getYearMonthPath(Path path) {
    Path year = path.getParent().getParent().getFileName();
    Path month = path.getParent().getFileName();
    return year.resolve(month);
  }

  public Path getFileUploadPath(String path) {
    YearMonth yearMonth = getYearMonth(path);
    return getBackupPath().resolve(yearMonth.year() + "_" + yearMonth.month() + ".zip");
  }

  public Path getDownloadDestination(String filename) {
    return getDownloadPath().resolve(Path.of(filename));
  }

  public boolean matchesYearMonthPattern(Path path) {
    return yearMonthPattern.matcher(path.toString()).find();
  }

  private void verifyPhotoFolder(String path) {
    Objects.requireNonNull(path);
    assert Arrays.stream(path.split(File.separator)).count() == 2;
  }
}
