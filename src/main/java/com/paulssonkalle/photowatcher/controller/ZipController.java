package com.paulssonkalle.photowatcher.controller;

import com.paulssonkalle.photowatcher.domain.YearMonth;
import com.paulssonkalle.photowatcher.domain.ZipFileDetail;
import com.paulssonkalle.photowatcher.service.ZipService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/zip")
public class ZipController {

  private final ZipService zipService;

  @PostMapping
  public ResponseEntity<ZipFileDetail> zip(@Valid @RequestBody YearMonth yearMonth) {
    try {
      ZipFileDetail zipFileDetail =
          zipService.zipFolder(Paths.get(yearMonth.year(), yearMonth.month()).toString());
      return ResponseEntity.ok(zipFileDetail);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return ResponseEntity.internalServerError().build();
    }
  }
}
