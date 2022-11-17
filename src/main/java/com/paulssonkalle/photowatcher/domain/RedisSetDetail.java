package com.paulssonkalle.photowatcher.domain;

import java.util.List;

public record RedisSetDetail(List<String> zip, List<String> upload, List<String> download) {}
