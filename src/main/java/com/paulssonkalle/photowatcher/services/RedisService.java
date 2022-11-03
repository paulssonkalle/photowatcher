package com.paulssonkalle.photowatcher.services;

import java.nio.file.Path;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {
  private static final Pattern yearMonthPattern = Pattern.compile("\\d{4}/(0[1-9]|1[0-2])");
  private final BoundSetOperations<String, String> zipSetOps;
  private final BoundSetOperations<String, String> uploadSetOps;
  private final PhotoPathService photoPathService;

  public void addPath(Path path) {
    Path yearMonthPath = photoPathService.getYearMonthPath(path);
    if (yearMonthPattern.matcher(yearMonthPath.toString()).find()) {
      log.info("Adding {} as changed", yearMonthPath);
      zipSetOps.add(yearMonthPath.toString());
    } else {
      log.info("{} did not match year and month pattern, not adding as changed", yearMonthPath);
    }
  }

  public Set<String> getZipMembers() {
    return zipSetOps.members();
  }

  public Long removeZipMember(String key) {
    return zipSetOps.remove(key);
  }

  public Long addZipMember(String value) {
    return zipSetOps.add(value);
  }

  public Set<String> getUploadMembers() {
    return uploadSetOps.members();
  }

  public Long removeUploadMember(String key) {
    return uploadSetOps.remove(key);
  }

  public Long addUploadMember(String value) {
    return uploadSetOps.add(value);
  }
}
