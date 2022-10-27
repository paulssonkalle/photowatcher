package com.paulssonkalle.photowatcher.tasks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduledTasks {
  @Value("${redis.changed}") private String KEY;

  private final ReactiveRedisTemplate<String, String> redis;

  public ScheduledTasks(ReactiveRedisTemplate<String, String> redis) {
    this.redis = redis;
  }

  @Scheduled(fixedRate = 5000)
  public void checkRedis() {
    log.info("Checking Redis");
    redis.opsForSet().members(KEY).subscribe(this::handleChange);
  }

  private void handleChange(String path) {
    log.info("Zipping {}", path);
    log.info("Uploading {}", path);
    redis.opsForSet().remove(KEY, path).subscribe();
    log.info("Removed {}", path);
  }
}
