package com.paulssonkalle.photowatcher.services;

import java.nio.file.Path;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {
  private final BoundSetOperations<String, String> zipSetOps;
  private final BoundSetOperations<String, String> uploadSetOps;

  public boolean addPath(Path path) {
    return addZipMember(path.toString()) > 0;
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
