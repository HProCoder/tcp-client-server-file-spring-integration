package com.hprocoder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLengthHeaderSerializer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class ServerConfig {

  private static final int SERVER_PORT = 12345;

  @Bean
  public TcpNetServerConnectionFactory serverConnectionFactory(){
    TcpNetServerConnectionFactory serverFactory = new TcpNetServerConnectionFactory(SERVER_PORT);
    serverFactory.setSerializer(new ByteArrayLengthHeaderSerializer());
    serverFactory.setDeserializer(new ByteArrayLengthHeaderSerializer());
    return serverFactory;
  }

  @Bean
  public MessageChannel requestChannel(){
    return new DirectChannel();
  }

  @Bean
  public TcpInboundGateway tcpInboundGateway(){
    TcpInboundGateway tcpInboundGateway = new TcpInboundGateway();
    tcpInboundGateway.setConnectionFactory(serverConnectionFactory());
    tcpInboundGateway.setRequestChannel(requestChannel());
    return tcpInboundGateway;
  }
  @Bean
  public IntegrationFlow serverFlow(MessageHandler messageHandler){
    return IntegrationFlow.from(requestChannel())
        .handle(messageHandler)
        .get();
  }

  @Bean
  public MessageHandler messageHandler(){
    return new ServerResponseHandler();
  }

}
