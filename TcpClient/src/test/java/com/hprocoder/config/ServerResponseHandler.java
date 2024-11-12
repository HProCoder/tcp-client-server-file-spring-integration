package com.hprocoder.config;

import com.hprocoder.enums.ResponseStatus;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.GenericMessage;

public class ServerResponseHandler implements MessageHandler {

  public static final String filename = "testfile.txt";
  public static int filelength;

  @Override
  public void handleMessage(Message message) throws MessagingException {
    byte[] receivedBytes = (byte[]) message.getPayload();
    String fileId = parseFileId(receivedBytes);
    System.out.println("fileid recieved : " + fileId);

    Path filePath = null;
    try {
      filePath = Path.of(getClass().getClassLoader().getResource("files/"+filename).toURI());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }

// Read the file content as byte array
    try {
      byte[] fileBytes = Files.readAllBytes(filePath);
      byte[] response;
// Traitement de la réponse
      byte[] fileContent = "Contenu du fichier demandé".getBytes();

      if("1".equals(fileId)){
        response = sendOkResponse(ResponseStatus.OK, filename, fileBytes);
      }else{
        response = sendKoResponse(ResponseStatus.KO);
      }

// envoi de la réponse
      MessageChannel m = (MessageChannel) message.getHeaders().getReplyChannel();
      m.send(new GenericMessage(response));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  private String parseFileId(byte[] receivedBytes) {
// Read the length of the ID from the message (4 bytes)
    ByteBuffer buffer = ByteBuffer.wrap(receivedBytes);
    buffer.order(ByteOrder.LITTLE_ENDIAN);

// Skip the first part of the header (starting byte, commande, and size of ID)
    buffer.get(); // Skip the starting byte
    buffer.get(); // Skip the commande byte
    buffer.get(); // Skip the size byte

// Read the length of the ID (4 bytes)
    int idLength = buffer.getInt();

// Read the file ID itself (variable length)
    byte[] fileIdBytes = new byte[idLength];
    buffer.get(fileIdBytes);

// Convert the file ID to a String
    return new String(fileIdBytes, StandardCharsets.UTF_8);
  }

  public byte [] sendKoResponse(ResponseStatus status){
    ByteBuffer buffer = ByteBuffer.allocate(1 + 1 + 1);
    buffer.order(ByteOrder.BIG_ENDIAN);
    buffer.put((byte) 3);
    buffer.put((byte) status.getValue());
    buffer.put((byte) 1);

    return buffer.array();
  }

  public byte[] sendOkResponse(ResponseStatus status, String filename, byte[] fileContent) {
    try {
// Calculate the total length: 1 (length byte) + 1 (status byte) + 4 (filename length) + filename bytes + 1 (null terminator) + 4 (file length) + file content bytes
      int totalLength = 1 + 1 + 1 + 4 + filename.getBytes(StandardCharsets.UTF_8).length + 1 + 1 + 4 + fileContent.length;

      ByteBuffer buffer = ByteBuffer.allocate(totalLength);
      buffer.order(ByteOrder.BIG_ENDIAN);

// Length byte
      buffer.put((byte) totalLength);

// Status byte
      buffer.put((byte) status.getValue()); // Assuming `status.getValue()` returns a byte representation

      buffer.order(ByteOrder.LITTLE_ENDIAN);
      buffer.put((byte)1);// TAILLE TAILLE_FILENAME
// File name length (4 bytes, little endian)
      byte[] filenameBytes = filename.getBytes(StandardCharsets.UTF_8);
      buffer.putInt(filenameBytes.length);

// Filename (variable length)
      buffer.put(filenameBytes);

// FILENAME caractère NULL de fin de chaine
      buffer.put((byte) 0);
// TAILLE TAILLE_FILE
      buffer.put((byte) 1);

// TAILLE_FILE
      filelength = fileContent.length;
      buffer.putInt(filelength);

// File content
      buffer.put(fileContent);

      return buffer.array();// = 46
    } catch (Exception e) {
      throw new RuntimeException("Error creating response", e);
    }
  }
}
