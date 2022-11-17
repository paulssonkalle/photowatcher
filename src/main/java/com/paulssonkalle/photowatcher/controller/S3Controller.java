package com.paulssonkalle.photowatcher.controller;

import com.paulssonkalle.photowatcher.domain.BucketFileDetail;
import com.paulssonkalle.photowatcher.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
@Slf4j
public class S3Controller {
  private final S3Service s3Service;

  @GetMapping("/list")
  public Flux<BucketFileDetail> list() {
    return s3Service.listObjects();
  }

  @PostMapping("/restore/{filename}")
  public Mono<Void> restore(@PathVariable String filename) {
    return s3Service.restore(filename);
  }

  @PostMapping("/restore/all")
  public Mono<Void> restoreAll() {
    return s3Service.restoreAll();
  }
}
