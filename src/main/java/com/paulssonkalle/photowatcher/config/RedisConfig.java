package com.paulssonkalle.photowatcher.config;

import com.paulssonkalle.photowatcher.config.properties.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@RequiredArgsConstructor
public class RedisConfig {
  @Bean
  public SetOperations<String, String> zipSetOps(RedisTemplate<String, String> redisTemplate) {
    return redisTemplate.opsForSet();
  }
}
