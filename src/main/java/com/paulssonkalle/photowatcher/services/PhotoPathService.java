package com.paulssonkalle.photowatcher.services;

import com.paulssonkalle.photowatcher.domain.YearMonth;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class PhotoPathService {
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

  private void verifyPhotoFolder(@NonNull String path) {
    Objects.requireNonNull(path);
    assert Arrays.stream(path.split(File.separator)).count() == 2;
  }
}
