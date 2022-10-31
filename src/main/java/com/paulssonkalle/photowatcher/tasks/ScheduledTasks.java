package com.paulssonkalle.photowatcher.tasks;

import com.paulssonkalle.photowatcher.domain.YearMonth;
import com.paulssonkalle.photowatcher.services.PhotoPathService;
import com.paulssonkalle.photowatcher.services.S3Service;
import com.paulssonkalle.photowatcher.services.ZipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledTasks {
  @Value("${app.redis.keys.zip}") private String zipKey;

  @Value("${app.redis.keys.upload}") private String uploadKey;

  @Value("${app.dryrun}") private boolean dryRun;

  private final ReactiveRedisTemplate<String, String> redis;
  private final ZipService zipService;
  private final S3Service s3Service;
  private final PhotoPathService photoPathService;

  @Scheduled(cron = "${app.schedules.zip}")
  public void checkZip() {
    redis.opsForSet().members(zipKey).subscribe(this::zip);
  }

  @Scheduled(cron = "${app.schedules.upload}")
  public void checkUpload() {
    redis.opsForSet().members(uploadKey).subscribe(this::upload);
  }

  private void zip(String path) {
    final YearMonth yearMonth = photoPathService.getYearMonth(path);
    if (dryRun) {
      log.info("Zipping disabled. Not zipping {}", yearMonth);
    } else {
      zipService.zipPhotoFolder(yearMonth.year(), yearMonth.month());
    }
    log.info("Marking {} as zipped", path);
    redis.opsForSet().remove(zipKey, path).subscribe();
    redis.opsForSet().add(uploadKey, path).subscribe();
  }

  private void upload(String path) {
    final YearMonth yearMonth = photoPathService.getYearMonth(path);
    if (dryRun) {
      log.info("Uploading disabled. Not uploading {}", yearMonth);
    } else {
      log.info("Uploading {}", path);
      s3Service.uploadPhotos(yearMonth.year(), yearMonth.month());
    }
    log.info("Marking {} as uploaded", path);
    redis.opsForSet().remove(uploadKey, path).subscribe();
  }
}
