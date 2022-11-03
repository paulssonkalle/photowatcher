package com.paulssonkalle.photowatcher.tasks;

import com.paulssonkalle.photowatcher.services.RedisService;
import com.paulssonkalle.photowatcher.services.S3Service;
import com.paulssonkalle.photowatcher.services.ZipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledTasks {
  @Value("${app.dryrun}") private boolean dryRun;

  private final RedisService redisService;
  private final ZipService zipService;
  private final S3Service s3Service;

  @Scheduled(cron = "${app.schedules.zip}")
  public void checkZip() {
    redisService.getZipMembers().forEach(this::zip);
  }

  @Scheduled(cron = "${app.schedules.upload}")
  public void checkUpload() {
    redisService.getUploadMembers().forEach(this::upload);
  }

  private void zip(String path) {
    if (dryRun) {
      log.info("Zipping disabled. Not zipping {}", path);
      redisService.removeZipMember(path);
    } else {
      redisService.removeZipMember(path);
      zipService
          .zipFolder(path)
          .whenComplete(
              (result, ex) -> {
                if (ex != null) {
                  log.error("Failed to zip {}", path, ex);
                  redisService.addZipMember(path);
                }
              });
    }
  }

  private void upload(String path) {
    if (dryRun) {
      log.info("Uploading disabled. Not uploading {}", path);
      redisService.removeUploadMember(path);
    } else {
      log.info("Uploading {}", path);
      redisService.removeUploadMember(path);
      s3Service
          .upload(path)
          .whenComplete(
              (response, exception) -> {
                if (response != null && response.sdkHttpResponse().isSuccessful()) {
                  log.info("Uploaded {} successfully", path);
                  redisService.removeUploadMember(path);
                } else {
                  log.error("Failed to upload {}", path, exception);
                  redisService.addUploadMember(path);
                }
              });
    }
  }
}
