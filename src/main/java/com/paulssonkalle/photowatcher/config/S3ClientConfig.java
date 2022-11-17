package com.paulssonkalle.photowatcher.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3AsyncClient;

@Configuration
public class S3ClientConfig {
  @Bean
  public S3AsyncClient s3AsyncClient() {
    return S3AsyncClient.create();
  }
}
