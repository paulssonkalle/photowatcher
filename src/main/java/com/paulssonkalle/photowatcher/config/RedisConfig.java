package com.paulssonkalle.photowatcher.config;

import com.paulssonkalle.photowatcher.config.properties.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveSetOperations;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@RequiredArgsConstructor
public class RedisConfig {
  @Bean
  public ReactiveSetOperations<String, String> zipSetOps(
      ReactiveRedisTemplate<String, String> redisTemplate) {
    return redisTemplate.opsForSet();
  }
}
