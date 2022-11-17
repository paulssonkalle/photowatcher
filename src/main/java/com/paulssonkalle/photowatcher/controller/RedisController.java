package com.paulssonkalle.photowatcher.controller;

import com.paulssonkalle.photowatcher.domain.RedisSetDetail;
import com.paulssonkalle.photowatcher.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/redis")
public class RedisController {
  private final RedisService redisService;

  @GetMapping
  public Mono<RedisSetDetail> get() {
    return redisService.getSetDetails();
  }
}
