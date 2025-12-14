package com.paulssonkalle.photowatcher.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.paths")
public record PathProperties(String backup, String photos, String download) {}
