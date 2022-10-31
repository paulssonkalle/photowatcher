package com.paulssonkalle.photowatcher.services;

import java.nio.file.Path;
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

  public void uploadPhotos(String year, String month) {
    final Path fileToUpload = backupPath.resolve(year + "_" + month + ".zip");
    final String filename = fileToUpload.getFileName().toString();

    log.info("Starting upload of {}", filename);
    s3Client
        .putObject(
            builder ->
                builder.bucket(bucketName).key(filename).storageClass(StorageClass.DEEP_ARCHIVE),
            AsyncRequestBody.fromFile(fileToUpload))
        .whenComplete(
            (response, exception) -> handleCompletedUpload(response, exception, fileToUpload))
        .join();
  }

  private void handleCompletedUpload(PutObjectResponse response, Throwable exception, Path file) {
    if (response != null && response.sdkHttpResponse().isSuccessful()) {
      log.info("Uploaded {} successfully", file.getFileName());
    } else {
      log.error("Failed to upload {}", file.getFileName(), exception);
    }
  }
}
