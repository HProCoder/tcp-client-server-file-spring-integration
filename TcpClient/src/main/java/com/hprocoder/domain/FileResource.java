package com.hprocoder.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.Resource;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileResource {

  Resource resourceFile;
  int contentLength;
  String filename;
}
