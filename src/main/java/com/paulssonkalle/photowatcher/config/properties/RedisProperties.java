package com.paulssonkalle.photowatcher.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.redis.keys")
public record RedisProperties(String zipKey, String uploadKey) {}
