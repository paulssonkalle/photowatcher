package com.paulssonkalle.photowatcher.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.redis")
public record RedisProperties(Keys keys) {
  public record Keys(String zip, String upload, String download) {}
}
