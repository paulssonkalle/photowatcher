package com.paulssonkalle.photowatcher.controllers;

import com.paulssonkalle.photowatcher.services.RedisService;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/info")
public class InfoController {
  private final RedisService redisService;

  @GetMapping
  public Map<String, Set<String>> get() {
    var zipMembers = redisService.getZipMembers();
    var uploadMembers = redisService.getUploadMembers();
    return Map.of("zip", zipMembers, "upload", uploadMembers);
  }
}
