package com.hprocoder.service;

import com.hprocoder.config.TcpClientGateway;
import com.hprocoder.domain.FileResource;
import com.hprocoder.exception.TechnicalException;
import com.hprocoder.utils.TechnicalMessageUtils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {

  private final TcpClientGateway tcpClientGateway;

  @Override
  public FileResource getFileById(String fileId) {
    byte[] request = buildMessage((byte) 1, fileId);
    try {
      return tcpClientGateway.send(request);
    } catch (Exception e) {
      throw new TechnicalException(TechnicalMessageUtils.ENABLE_TO_FIND_FILE_WITH_GIVEN_ID);
    }
  }

  private byte[] buildMessage(byte command, String id) {
    byte[] idByte = id.getBytes(StandardCharsets.UTF_8);
    return ByteBuffer.allocate(11 + idByte.length) // 10 octets fixe + 1 octet for NULL
        .order(ByteOrder.BIG_ENDIAN) // Big-endian for the message size
        .put((byte) 1)
        .put(command) // COMMAND
        .order(ByteOrder.LITTLE_ENDIAN) // Little-endian for ID
        .put((byte) 4) // length of field length_ID
        .putInt(idByte.length) // length of ID
        .put(idByte) // ID
        .put((byte) 0) //  NULL character at the end
        .array();
  }
}
