package com.paulssonkalle.photowatcher.services;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RedisService {

    @Value("${redis.changed}")
    private String KEY;
    private static final Pattern yearMonthPattern = Pattern.compile("\\d{4}/(0[1-9]|1[0-2])");
    private final ReactiveRedisTemplate<String, String> redis;

    public RedisService(ReactiveRedisTemplate<String, String> redis) {
        this.redis = redis;
    }

    public void addChange(WatchEvent<?> event, Path path) {
        Path yearMonthPath = getYearMonthPath(path);
        if (yearMonthPattern.matcher(yearMonthPath.toString()).find()) {
            log.info("Adding {} as changed", yearMonthPath);
            redis.opsForSet().add(KEY, yearMonthPath.toString()).subscribe();
        } else {
            log.info("{} did not match year and month pattern, not adding as changed", yearMonthPath);
        }
    }

    private Path getYearMonthPath(Path path) {
        var year = path.getParent().getParent().getFileName();
        var month = path.getParent().getFileName();
        return year.resolve(month);
    }
}
