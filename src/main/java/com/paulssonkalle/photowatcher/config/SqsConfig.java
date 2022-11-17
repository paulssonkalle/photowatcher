package com.paulssonkalle.photowatcher.config;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.serialization.PojoSerializer;
import com.amazonaws.services.lambda.runtime.serialization.events.LambdaEventSerializers;
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
public class SqsConfig {

  @Bean
  SqsMessageListenerContainerFactory<Object> restoreQueueFactory(SqsAsyncClient sqsAsyncClient) {
    return SqsMessageListenerContainerFactory.builder()
        .configure(
            options ->
                options
                    .acknowledgementMode(AcknowledgementMode.ON_SUCCESS)
                    .pollTimeout(Duration.ofSeconds(20)))
        .sqsAsyncClient(sqsAsyncClient)
        .build();
  }

  @Bean
  PojoSerializer<S3Event> s3EventSerializer() {
    return LambdaEventSerializers.serializerFor(
        S3Event.class, Thread.currentThread().getContextClassLoader());
  }
}
