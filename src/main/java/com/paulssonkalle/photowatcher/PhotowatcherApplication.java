package com.paulssonkalle.photowatcher;

import com.paulssonkalle.photowatcher.service.PathService;
import com.paulssonkalle.photowatcher.service.WatchServiceListener;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
@ConfigurationPropertiesScan("com.paulssonkalle.photowatcher.config.properties")
public class PhotowatcherApplication implements ApplicationRunner {
  private final WatchServiceListener watchServiceListener;
  private final PathService pathService;

  static void main(String[] args) {
    SpringApplication.run(PhotowatcherApplication.class, args);
  }

  @Override
  public void run(@NonNull ApplicationArguments args) throws IOException, InterruptedException {
    try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
      watchServiceListener.registerDirectory(pathService.getPhotosPath(), watchService);
      watchServiceListener.startListening(watchService);
    } catch (IOException | ClosedWatchServiceException | InterruptedException e) {
      log.error("Watchservice failed", e);
      throw e;
    }
  }
}
