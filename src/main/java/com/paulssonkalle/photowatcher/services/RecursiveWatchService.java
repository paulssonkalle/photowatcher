package com.paulssonkalle.photowatcher.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class RecursiveWatchService {
  private static final Pattern yearMonthPattern = Pattern.compile("\\d{4}/(0[1-9]|1[0-2])");
  private final Map<WatchKey, Path> keyPathMap = new HashMap<>();
  private final RedisService redisService;
  private final PhotoPathService photoPathService;

  public void startListening(WatchService watchService)
      throws InterruptedException, IOException, ClosedWatchServiceException {
    do {
      WatchKey queuedKey = watchService.take();
      for (WatchEvent<?> watchEvent : queuedKey.pollEvents()) {
        Path path = (Path) watchEvent.context();
        Path parentPath = keyPathMap.get(queuedKey);
        path = parentPath.resolve(path);
        Path yearMonthPath = photoPathService.getYearMonthPath(path);

        if (!yearMonthPattern.matcher(yearMonthPath.toString()).find()) {
          log.error(
              "{} did not match year and month pattern, not adding as changed", yearMonthPath);
        } else if (redisService.addPath(yearMonthPath)) {
          log.info("Adding {} as changed", yearMonthPath);
        }

        if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
          registerDirectory(path, watchService);
        }
      }
      if (!queuedKey.reset()) {
        keyPathMap.remove(queuedKey);
      }
    } while (!keyPathMap.isEmpty());
  }

  public void registerDirectory(Path path, WatchService watchService) throws IOException {
    if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
      return;
    }

    WatchKey key =
        path.register(
            watchService,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_MODIFY,
            StandardWatchEventKinds.ENTRY_DELETE);
    keyPathMap.put(key, path);
    log.info("Registered {}", path);
    for (File file : Objects.requireNonNull(path.toFile().listFiles())) {
      registerDirectory(file.toPath(), watchService);
    }
  }
}
