package com.paulssonkalle.photowatcher.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class FileSize {
  private FileSize() {
    throw new IllegalStateException("Utility class");
  }

  // From: https://programming.guide/worlds-most-copied-so-snippet.html
  public static String humanReadableByteCountBin(long bytes) {
    long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
    if (absB < 1024) {
      return bytes + " B";
    }
    long value = absB;
    CharacterIterator ci = new StringCharacterIterator("KMGTPE");
    for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
      value >>= 10;
      ci.next();
    }
    value *= Long.signum(bytes);
    return String.format("%.1f %ciB", value / 1024.0, ci.current());
  }
}
