package com.paulssonkalle.photowatcher.task;

import com.paulssonkalle.photowatcher.domain.ZipFileDetail;
import com.paulssonkalle.photowatcher.service.RedisService;
import com.paulssonkalle.photowatcher.service.S3Service;
import com.paulssonkalle.photowatcher.service.ZipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledTasks {
  private final RedisService redisService;
  private final ZipService zipService;
  private final S3Service s3Service;

  @Scheduled(cron = "${app.scheduling.cron.zip}")
  public void checkZip() {
    redisService
        .getZipMembers()
        .map(
            filename ->
                Mono.just(filename)
                    .flatMap(this::zip)
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe(
                        zipFileDetail -> {
                          log.info(
                              "Successfully zipped {} to {}",
                              zipFileDetail.sourceFolder(),
                              zipFileDetail.destinationFile());
                          redisService.removeZipMember(filename).subscribe();
                          redisService.addUploadMember(filename).subscribe();
                        },
                        throwable ->
                            log.error("Failed to create zip file for {}", filename, throwable)))
        .subscribeOn(Schedulers.boundedElastic())
        .subscribe();
  }

  @Scheduled(cron = "${app.scheduling.cron.upload}")
  public void checkUpload() {
    redisService
        .getUploadMembers()
        .map(
            filename ->
                Mono.just(filename)
                    .flatMap(this::upload)
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe(
                        response -> {
                          if (response.sdkHttpResponse().isSuccessful()) {
                            log.info("Uploaded {} successfully", filename);
                            redisService.removeUploadMember(filename).subscribe();
                          }
                        },
                        throwable -> log.error("Failed to upload {}", filename, throwable)))
        .subscribeOn(Schedulers.boundedElastic())
        .subscribe();
  }

  @Scheduled(cron = "${app.scheduling.cron.download}")
  public void checkDownload() {
    redisService
        .getDownloadMembers()
        .map(
            filename ->
                Mono.just(filename)
                    .flatMap(this::download)
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe(
                        response -> {
                          if (response.sdkHttpResponse().isSuccessful()) {
                            log.info("Downloaded {} successfully", filename);
                            redisService.removeDownloadMember(filename).subscribe();
                          }
                        },
                        throwable -> log.error("Failed to download {}", filename, throwable)))
        .subscribeOn(Schedulers.boundedElastic())
        .subscribe();
  }

  private Mono<ZipFileDetail> zip(String path) {
    return zipService.zipFolder(path);
  }

  private Mono<PutObjectResponse> upload(String path) {
    return s3Service.upload(path);
  }

  private Mono<GetObjectResponse> download(String filename) {
    return s3Service.download(filename);
  }
}
