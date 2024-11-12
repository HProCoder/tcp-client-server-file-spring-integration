package com.hprocoder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static  org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hprocoder.config.ServerResponseHandler;
import com.hprocoder.config.TestConfig;
import com.hprocoder.domain.FileResource;
import com.hprocoder.exception.TechnicalException;
import com.hprocoder.utils.TechnicalMessageUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestConfig.class)
class FileServiceImplTest {

  @Autowired
  FileService fileService;

  @Test
  void getFileById_Should_Return_OK() {
    String fileId = "1";
    FileResource fileResource = fileService.getFileById(fileId);
    assertNotNull(fileResource);
    assertEquals(ServerResponseHandler.filename, fileResource.getFilename());
    assertEquals(ServerResponseHandler.filelength, fileResource.getContentLength());
  }

  @Test
  void getFileById_Should_Return_KO() {
    String fileId = "2";
    TechnicalException exception = assertThrows(TechnicalException.class,
        () -> fileService.getFileById(fileId));

    assertEquals(TechnicalMessageUtils.ENABLE_TO_FIND_FILE_WITH_GIVEN_ID, exception.getMessage());
  }
}