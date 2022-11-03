package com.paulssonkalle.photowatcher.services;

import com.paulssonkalle.photowatcher.domain.YearMonth;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.StorageClass;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

  private final S3AsyncClient s3Client;

  @Value("${app.aws.bucket}") private String bucketName;

  @Value("${app.paths.backup}") private Path backupPath;

  private final PhotoPathService photoPathService;

  public CompletableFuture<PutObjectResponse> upload(String path) {
    final YearMonth yearMonth = photoPathService.getYearMonth(path);
    final Path fileToUpload =
        backupPath.resolve(yearMonth.year() + "_" + yearMonth.month() + ".zip");
    final String filename = fileToUpload.getFileName().toString();

    log.info("Starting upload of {}", filename);
    return s3Client.putObject(
        builder -> builder.bucket(bucketName).key(filename).storageClass(StorageClass.DEEP_ARCHIVE),
        AsyncRequestBody.fromFile(fileToUpload));
  }
}
