package com.paulssonkalle.photowatcher.domain;

import java.time.Instant;
import lombok.Builder;

@Builder
public record BucketFileDetail(
    String filename, Instant lastModified, String size, String storageClass) {}
