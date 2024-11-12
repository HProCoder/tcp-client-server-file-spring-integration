package com.hprocoder.config;

import com.hprocoder.domain.FileResource;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "toChannel")
public interface TcpClientGateway {

  FileResource send(byte[] message);

}
