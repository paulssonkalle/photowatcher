package com.paulssonkalle.photowatcher.service;

import static software.amazon.awssdk.services.s3.model.StorageClass.DEEP_ARCHIVE;

import com.paulssonkalle.photowatcher.config.properties.AwsProperties;
import com.paulssonkalle.photowatcher.domain.BucketFileDetail;
import com.paulssonkalle.photowatcher.util.FileSize;
import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileDownload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

  private final S3AsyncClient s3Client;
  private final S3TransferManager s3TransferManager;
  private final AwsProperties awsProperties;
  private final PathService pathService;

  public PutObjectResponse upload(String path) {
    final Path fileToUpload = pathService.getFileUploadPath(path);
    final String filename = fileToUpload.getFileName().toString();
    log.info("Starting upload of {}", filename);
    UploadFileRequest uploadFileRequest =
        UploadFileRequest.builder()
            .putObjectRequest(
                builder ->
                    builder
                        .bucket(awsProperties.s3().bucketName())
                        .key(filename)
                        .storageClass(DEEP_ARCHIVE))
            .source(fileToUpload)
            .build();
    PutObjectResponse response =
        s3TransferManager.uploadFile(uploadFileRequest).completionFuture().join().response();
    log.info("Finished uploading of {}", filename);
    return response;
  }

  public List<BucketFileDetail> listObjects() {
    // 1000 objects per request should be plenty, but would be nice to handle pagination
    return s3Client
        .listObjectsV2(builder -> builder.bucket(awsProperties.s3().bucketName()))
        .join()
        .contents()
        .stream()
        .map(
            o ->
                BucketFileDetail.builder()
                    .filename(o.key())
                    .lastModified(o.lastModified())
                    .size(FileSize.humanReadableByteCountBin(o.size()))
                    .storageClass(o.storageClassAsString())
                    .build())
        .toList();
  }

  public void restore(String filename) {
    log.info("Starting restore of {}", filename);
    RestoreObjectResponse restoreObjectResponse =
        s3Client
            .restoreObject(
                builder ->
                    builder
                        .bucket(awsProperties.s3().bucketName())
                        .key(filename)
                        .restoreRequest(
                            RestoreRequest.builder()
                                .days(awsProperties.s3().restoreDurationInDays())
                                .build())
                        .build())
            .join();
    if (restoreObjectResponse.sdkHttpResponse().isSuccessful()) {
      log.info("Finished restore of {}", filename);
    } else {
      log.warn("Failed to restore of {}", filename);
    }
  }

  public void restoreAll() {
    log.info("Starting restore all");
    List<BucketFileDetail> bucketFileDetails = listObjects();
    for (BucketFileDetail bucketFileDetail : bucketFileDetails) {
      restore(bucketFileDetail.filename());
    }
    log.info("Finished restore all");
  }

  public GetObjectResponse download(String filename) {
    log.info("Starting download of {}", filename);
    Path downloadDestination = pathService.getDownloadDestination(filename);
    CompletedFileDownload completedFileDownload =
        s3TransferManager
            .downloadFile(
                builder ->
                    builder
                        .getObjectRequest(
                            getObjectRequestBuilder ->
                                getObjectRequestBuilder
                                    .bucket(awsProperties.s3().bucketName())
                                    .key(filename))
                        .destination(downloadDestination)
                        .build())
            .completionFuture()
            .join();

    if (completedFileDownload.response().sdkHttpResponse().isSuccessful()) {
      log.info("Finished download of {}", filename);
    } else {
      log.warn("Failed to download of {}", filename);
    }

    return completedFileDownload.response();
  }
}
