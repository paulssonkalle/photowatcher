package com.paulssonkalle.photowatcher.service;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.serialization.PojoSerializer;
import io.awspring.cloud.sqs.annotation.SqsListener;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.Message;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestoreObjectListener {

  private static final String RESTORE_COMPLETE = "ObjectRestore:Completed";
  private final RedisService redis;
  private final PojoSerializer<S3Event> s3EventSerializer;

  @SqsListener(value = "${app.aws.sqs.queue-name}", factory = "restoreQueueFactory")
  public void queueListener(Message message) {
    S3Event event = s3EventSerializer.fromJson(message.body());
    for (var notificationRecord : event.getRecords()) {
      String key = notificationRecord.getS3().getObject().getKey();
      String urlDecodedKey = URLDecoder.decode(key, StandardCharsets.UTF_8);
      if (notificationRecord.getEventName().equals(RESTORE_COMPLETE)) {
        redis.addDownload(urlDecodedKey);
      }
      log.info("Received {} event for {}", notificationRecord.getEventName(), urlDecodedKey);
    }
  }
}
