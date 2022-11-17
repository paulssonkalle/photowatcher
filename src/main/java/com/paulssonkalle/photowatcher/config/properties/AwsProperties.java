package com.paulssonkalle.photowatcher.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.aws")
public record AwsProperties(S3 s3, Sqs sqs) {
  public record S3(String bucketName, Integer restoreDurationInDays) {}

  public record Sqs(String queueName) {}
}
