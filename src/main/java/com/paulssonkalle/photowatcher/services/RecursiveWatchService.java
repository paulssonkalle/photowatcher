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
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RecursiveWatchService {
    private Map<WatchKey, Path> keyPathMap = new HashMap<>();
    private final RedisService redisService;

    public RecursiveWatchService(RedisService redisService) {
        this.redisService = redisService;
    }

    public void startListening(WatchService watchService) throws InterruptedException, IOException, ClosedWatchServiceException {
        while (true) {
            WatchKey queuedKey = watchService.take();
            for (WatchEvent<?> watchEvent : queuedKey.pollEvents()) {
                Path path = (Path) watchEvent.context();
                Path parentPath = keyPathMap.get(queuedKey);
                path = parentPath.resolve(path);
                
                redisService.addChange(watchEvent, path);
 
                if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    registerDirectory(path, watchService);
                }
            }
            if (!queuedKey.reset()) {
                keyPathMap.remove(queuedKey);
            }
            if (keyPathMap.isEmpty()) {
                break;
            }
        }
    }

    public void registerDirectory(Path path, WatchService watchService) throws IOException {
        if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            return;
        }

        WatchKey key = path.register(watchService,
                            StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_MODIFY,
                            StandardWatchEventKinds.ENTRY_DELETE);
        keyPathMap.put(key, path);
        log.info("Registered {}", path);
        for (File file : path.toFile().listFiles()) {
            registerDirectory(file.toPath(), watchService);
        }
    }
}
