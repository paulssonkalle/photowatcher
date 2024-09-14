package com.paulssonkalle.photowatcher.service;

import com.paulssonkalle.photowatcher.config.properties.RedisProperties;
import com.paulssonkalle.photowatcher.domain.RedisSetDetail;
import java.nio.file.Path;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {
  private final RedisProperties properties;
  private final SetOperations<String, String> setOperations;

  public Long addPath(Path path) {
    return addZipMember(String.valueOf(path));
  }

  public Set<String> getZipMembers() {
    return setOperations.members(properties.keys().zip());
  }

  public Long removeZipMember(String value) {
    return setOperations.remove(properties.keys().zip(), value);
  }

  public Long addZipMember(String value) {
    return setOperations.add(properties.keys().zip(), value);
  }

  public Set<String> getUploadMembers() {
    return setOperations.members(properties.keys().upload());
  }

  public Long removeUploadMember(String value) {
    return setOperations.remove(properties.keys().upload(), value);
  }

  public Long addUploadMember(String value) {
    return setOperations.add(properties.keys().upload(), value);
  }

  public Long addDownload(String value) {
    return setOperations.add(properties.keys().download(), value);
  }

  public Set<String> getDownloadMembers() {
    return setOperations.members(properties.keys().download());
  }

  public Long removeDownloadMember(String value) {
    return setOperations.remove(properties.keys().download(), value);
  }

  public RedisSetDetail getSetDetails() {
    Set<String> zipMembers = getZipMembers();
    Set<String> uploadMembers = getUploadMembers();
    Set<String> downloadMembers = getDownloadMembers();

    return new RedisSetDetail(zipMembers, uploadMembers, downloadMembers);
  }
}
