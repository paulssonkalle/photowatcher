package com.paulssonkalle.photowatcher.task;

import com.paulssonkalle.photowatcher.service.RedisService;
import com.paulssonkalle.photowatcher.service.S3Service;
import com.paulssonkalle.photowatcher.service.ZipService;
import java.io.IOException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
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
  public void checkZip() throws IOException {
    Set<String> zipMembers = redisService.getZipMembers();
    for (String folder : zipMembers) {
      zipService.zipFolder(folder);
      redisService.removeZipMember(folder);
      redisService.addUploadMember(folder);
    }
  }

  @Scheduled(cron = "${app.scheduling.cron.upload}")
  public void checkUpload() {
    Set<String> uploadMembers = redisService.getUploadMembers();

    for (String filename : uploadMembers) {
      PutObjectResponse response = s3Service.upload(filename);
      if (response.sdkHttpResponse().isSuccessful()) {
        log.info("Uploaded {} successfully", filename);
        redisService.removeUploadMember(filename);
      }
    }
  }

  @Scheduled(cron = "${app.scheduling.cron.download}")
  public void checkDownload() {
    Set<String> downloadMembers = redisService.getDownloadMembers();

    for (String filename : downloadMembers) {
      GetObjectResponse response = s3Service.download(filename);
      if (response.sdkHttpResponse().isSuccessful()) {
        log.info("Downloaded {} successfully", filename);
        redisService.removeDownloadMember(filename);
      }
    }
  }
}
