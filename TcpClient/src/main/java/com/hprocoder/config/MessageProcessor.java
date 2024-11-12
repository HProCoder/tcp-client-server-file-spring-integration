package com.hprocoder.config;

import com.hprocoder.domain.FileResource;
import com.hprocoder.enums.ResponseStatus;
import com.hprocoder.utils.CustomFileUtils;
import com.hprocoder.utils.TechnicalMessageUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.integration.core.GenericHandler;
import org.springframework.messaging.MessageHeaders;

public class MessageProcessor implements GenericHandler<byte[]> {

  private static final Logger LOGGER = LogManager.getLogger(MessageProcessor.class);

  private static final String DIRECTORY = "D:/files";

  @Override
  public Object handle(byte[] payload, MessageHeaders headers) {
    FileResource response;
    ByteBuffer buffer = ByteBuffer.wrap(payload);
    buffer.get();
    ResponseStatus status = ResponseStatus.fromValue(buffer.get());

    try {
    if(ResponseStatus.OK.equals(status)){

        response = buildOkResponse(buffer);

    }else{
      LOGGER.error("Id not found");
      throw new RuntimeException("Id not found");
    }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return response;
  }

  private FileResource buildOkResponse(ByteBuffer buffer) throws IOException {
    buffer.get();// TAILLE TAILLE_FILENAME
    buffer.order(ByteOrder.LITTLE_ENDIAN);
// Check if we have enough bytes to read the file name length
    if (buffer.remaining() < Integer.BYTES) {
      throw new IOException(TechnicalMessageUtils.UNSUFFICIENT_DATA_READ_FILENAME_LENGTH);
    }

// TAILLE_FILENAME Uint 4 octets (little endian)
    int fileNameLength = buffer.getInt();
    LOGGER.info("Filename length: {}", fileNameLength);

// Validate that there are enough bytes remaining for the file name
    if (buffer.remaining() < fileNameLength) {
      throw new IOException(TechnicalMessageUtils.UNSUFFICIENT_DATA_READ_FILENAME);
    }

// FILENAME
    byte[] fileNameBytes = new byte[fileNameLength];
    buffer.get(fileNameBytes);
    String fileName = new String(fileNameBytes, StandardCharsets.UTF_8).trim();

    LOGGER.info("Filename: {}", fileName);

// Check if we have enough bytes to read the file content length
    if (buffer.remaining() < Integer.BYTES) {
      throw new IOException(TechnicalMessageUtils.UNSUFFICIENT_DATA_READ_FILE_CONTENT_LENGTH);
    }
// FILENAME caractère NULL de fin de chaine
    buffer.get();
// TAILLE TAILLE_FILE
    buffer.get();
// TAILLE_FILE
    int fileLength = buffer.getInt();

// Validate that there are enough bytes remaining for the file content
    if (buffer.remaining() < fileLength) {
      throw new IOException(TechnicalMessageUtils.UNSUFFICIENT_DATA_READ_FILE_CONTENT);
    }
// FILE
    byte[] fileContent = new byte[fileLength];
    buffer.get(fileContent);

    try {
      CustomFileUtils.saveFileInDisk(fileContent, fileName, DIRECTORY);
    } catch (IOException e) {
      throw new IOException(TechnicalMessageUtils.ENABLE_SAVE_FILE_IN_DISK);
    }

    LOGGER.info("File {} saved successfully with size: {} bytes", fileName, fileLength);
    ByteArrayResource resource = new ByteArrayResource(fileContent);

    return new FileResource(resource, fileLength, fileName);
  }
}
