package com.paulssonkalle.photowatcher.service;

import com.paulssonkalle.photowatcher.config.properties.RedisProperties;
import com.paulssonkalle.photowatcher.domain.RedisSetDetail;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveSetOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {
  private final RedisProperties properties;
  private final ReactiveSetOperations<String, String> setOperations;

  public Mono<Long> addPath(Path path) {
    return addZipMember(String.valueOf(path)).filter(l -> l > 0);
  }

  public Flux<String> getZipMembers() {
    return setOperations.members(properties.keys().zip());
  }

  public Mono<Long> removeZipMember(String value) {
    return setOperations.remove(properties.keys().zip(), value);
  }

  public Mono<Long> addZipMember(String value) {
    return setOperations.add(properties.keys().zip(), value);
  }

  public Flux<String> getUploadMembers() {
    return setOperations.members(properties.keys().upload());
  }

  public Mono<Long> removeUploadMember(String value) {
    return setOperations.remove(properties.keys().upload(), value);
  }

  public Mono<Long> addUploadMember(String value) {
    return setOperations.add(properties.keys().upload(), value);
  }

  public Mono<Long> addDownload(String value) {
    return setOperations.add(properties.keys().download(), value);
  }

  public Flux<String> getDownloadMembers() {
    return setOperations.members(properties.keys().download());
  }

  public Mono<Long> removeDownloadMember(String value) {
    return setOperations.remove(properties.keys().download(), value);
  }

  public Mono<RedisSetDetail> getSetDetails() {
    var zipMembers = getZipMembers().collectList();
    var uploadMembers = getUploadMembers().collectList();
    var downloadMembers = getDownloadMembers().collectList();

    return Mono.zip(zipMembers, uploadMembers, downloadMembers)
        .flatMap(
            tuple3 ->
                Mono.just(new RedisSetDetail(tuple3.getT1(), tuple3.getT2(), tuple3.getT3())));
  }
}
