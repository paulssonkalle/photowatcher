package com.paulssonkalle.photowatcher.controller;

import com.paulssonkalle.photowatcher.domain.BucketFileDetail;
import com.paulssonkalle.photowatcher.service.S3Service;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
@Slf4j
public class S3Controller {
  private final S3Service s3Service;

  @GetMapping("/list")
  public List<BucketFileDetail> list() {
    return s3Service.listObjects();
  }

  @PostMapping("/restore/{filename}")
  public void restore(@PathVariable String filename) {
    s3Service.restore(filename);
  }

  @PostMapping("/restore/all")
  public void restoreAll() {
    s3Service.restoreAll();
  }
}
