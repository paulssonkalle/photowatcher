package com.paulssonkalle.photowatcher.config.properties;

import java.nio.file.Path;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.paths")
public record PathProperties(Path backup, Path photos, Path download) {}
