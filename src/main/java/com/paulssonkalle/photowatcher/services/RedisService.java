package com.paulssonkalle.photowatcher.services;

import java.nio.file.Path;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {
  @Value("${app.redis.keys.zip}") private String zipKey;

  private static final Pattern yearMonthPattern = Pattern.compile("\\d{4}/(0[1-9]|1[0-2])");
  private final ReactiveRedisTemplate<String, String> redis;
  private final PhotoPathService photoPathService;

  public void addChange(Path path) {
    Path yearMonthPath = photoPathService.getYearMonthPath(path);
    if (yearMonthPattern.matcher(yearMonthPath.toString()).find()) {
      log.info("Adding {} as changed", yearMonthPath);
      redis.opsForSet().add(zipKey, yearMonthPath.toString()).subscribe();
    } else {
      log.info("{} did not match year and month pattern, not adding as changed", yearMonthPath);
    }
  }
}
