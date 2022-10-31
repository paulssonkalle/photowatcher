package com.paulssonkalle.photowatcher.domain;

import java.io.File;
import java.util.Objects;
import org.springframework.lang.NonNull;

public record YearMonth(@NonNull String year, @NonNull String month) {
  public YearMonth {
    Objects.requireNonNull(year);
    Objects.requireNonNull(month);
    assert year.length() == 3;
    assert month.length() == 1;
  }

  @Override
  public String toString() {
    return year + File.separator + month;
  }
}
