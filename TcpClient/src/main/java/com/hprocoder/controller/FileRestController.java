package com.hprocoder.controller;

import com.hprocoder.domain.FileResource;
import com.hprocoder.service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/file")
@AllArgsConstructor
public class FileRestController {

  private final FileService fileService;

  @GetMapping("/{id}")
  ResponseEntity<Resource> getFileById(@PathVariable("id") String fileId) {
    FileResource response = fileService.getFileById(fileId);

    return ResponseEntity.ok().contentLength(response.getContentLength())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + response.getFilename() + "\"")
        .body(response.getResourceFile());
  }

}
