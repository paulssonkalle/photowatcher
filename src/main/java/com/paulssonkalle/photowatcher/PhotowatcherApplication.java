package com.paulssonkalle.photowatcher;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.paulssonkalle.photowatcher.services.RecursiveWatchService;
import lombok.extern.slf4j.Slf4j;


@SpringBootApplication
@EnableScheduling
@Slf4j
public class PhotowatcherApplication implements ApplicationRunner {

    private final RecursiveWatchService recursiveWatchService;

    @Value("${paths.root}")
    private Path root;

    public PhotowatcherApplication(RecursiveWatchService recursiveWatchService) {
        this.recursiveWatchService = recursiveWatchService;
    }

    public static void main(String[] args) {
        SpringApplication.run(PhotowatcherApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            recursiveWatchService.registerDirectory(root, watchService);
            recursiveWatchService.startListening(watchService);
            log.info("Started listening");
        } catch (IOException | ClosedWatchServiceException | InterruptedException e) {
            log.error("Watchservice failed", e);
        }
    }

}
