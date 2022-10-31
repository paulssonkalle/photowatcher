package com.paulssonkalle.photowatcher;

import com.paulssonkalle.photowatcher.services.RecursiveWatchService;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class PhotowatcherApplication implements ApplicationRunner {

  private final RecursiveWatchService recursiveWatchService;

  @Value("${app.paths.photos}") private Path photos;

  public static void main(String[] args) {
    SpringApplication.run(PhotowatcherApplication.class, args);
  }

  @Override
  public void run(ApplicationArguments args) {
    try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
      recursiveWatchService.registerDirectory(photos, watchService);
      recursiveWatchService.startListening(watchService);
      log.info("Started listening");
    } catch (IOException | ClosedWatchServiceException | InterruptedException e) {
      log.error("Watchservice failed", e);
    }
  }
}
