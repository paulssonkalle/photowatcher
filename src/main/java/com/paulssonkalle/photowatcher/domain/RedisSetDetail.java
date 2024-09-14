package com.paulssonkalle.photowatcher.domain;

import java.util.Set;

public record RedisSetDetail(Set<String> zip, Set<String> upload, Set<String> download) {}
