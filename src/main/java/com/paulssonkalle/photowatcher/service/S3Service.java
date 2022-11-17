package com.paulssonkalle.photowatcher.service;

import static software.amazon.awssdk.services.s3.model.StorageClass.DEEP_ARCHIVE;

import com.paulssonkalle.photowatcher.config.properties.AwsProperties;
import com.paulssonkalle.photowatcher.domain.BucketFileDetail;
import com.paulssonkalle.photowatcher.util.FileSize;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

  private final S3AsyncClient s3Client;
  private final AwsProperties awsProperties;
  private final PathService pathService;

  public Mono<PutObjectResponse> upload(String path) {
    final Path fileToUpload = pathService.getFileUploadPath(path);
    final String filename = fileToUpload.getFileName().toString();
    log.info("Starting upload of {}", filename);
    return Mono.fromFuture(
        s3Client.putObject(
            builder ->
                builder
                    .bucket(awsProperties.s3().bucketName())
                    .key(filename)
                    .storageClass(DEEP_ARCHIVE),
            AsyncRequestBody.fromFile(fileToUpload)));
  }

  public Flux<BucketFileDetail> listObjects() {
    // 1000 objects per request should be plenty, but would be nice to handle pagination
    return Mono.fromFuture(
            s3Client.listObjectsV2(builder -> builder.bucket(awsProperties.s3().bucketName())))
        .flatMapIterable(ListObjectsV2Response::contents)
        .map(
            o ->
                BucketFileDetail.builder()
                    .filename(o.key())
                    .lastModified(o.lastModified())
                    .size(FileSize.humanReadableByteCountBin(o.size()))
                    .storageClass(o.storageClassAsString())
                    .build());
  }

  public Mono<Void> restore(String filename) {
    return Mono.fromFuture(
            s3Client.restoreObject(
                builder ->
                    builder
                        .bucket(awsProperties.s3().bucketName())
                        .key(filename)
                        .restoreRequest(
                            restoreRequest ->
                                restoreRequest.days(awsProperties.s3().restoreDurationInDays()))
                        .build()))
        .then();
  }

  public Mono<Void> restoreAll() {
    return listObjects().flatMap(fileDetail -> restore(fileDetail.filename())).then();
  }

  public Mono<GetObjectResponse> download(String filename) {
    log.info("Starting download of {}", filename);
    return Mono.just(pathService.getDownloadDestination(filename))
        .flatMap(
            downloadDestination ->
                Mono.fromFuture(
                    s3Client.getObject(
                        builder -> builder.bucket(awsProperties.s3().bucketName()).key(filename),
                        downloadDestination)));
  }
}
