package com.paulssonkalle.photowatcher.config;

import com.paulssonkalle.photowatcher.config.properties.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@RequiredArgsConstructor
public class RedisConfig {
  private final RedisProperties properties;

  @Bean
  public BoundSetOperations<String, String> zipSetOps(RedisTemplate<String, String> redisTemplate) {
    return redisTemplate.boundSetOps(properties.zipKey());
  }

  @Bean
  public BoundSetOperations<String, String> uploadSetOps(
      RedisTemplate<String, String> redisTemplate) {
    return redisTemplate.boundSetOps(properties.uploadKey());
  }
}
