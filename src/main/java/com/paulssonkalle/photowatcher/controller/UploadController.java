package com.paulssonkalle.photowatcher.controller;

import com.paulssonkalle.photowatcher.domain.YearMonth;
import com.paulssonkalle.photowatcher.service.S3Service;
import jakarta.validation.Valid;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Slf4j
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

  private final S3Service s3Service;

  @PostMapping
  public ResponseEntity<Void> upload(@Valid @RequestBody YearMonth yearMonth) {
    PutObjectResponse response =
        s3Service.upload(Path.of(yearMonth.year(), yearMonth.month()).toString());
    return ResponseEntity.status(response.sdkHttpResponse().statusCode()).build();
  }
}
