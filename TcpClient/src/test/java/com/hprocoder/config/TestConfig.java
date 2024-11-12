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
public class TestConfig {

  private static final int TEST_PORT = 12345;

  @Bean
  public TcpNetServerConnectionFactory serverConnectionFactory() {
    TcpNetServerConnectionFactory serverFactory = new TcpNetServerConnectionFactory(TEST_PORT);
    serverFactory.setSerializer(new ByteArrayLengthHeaderSerializer());
    serverFactory.setDeserializer(new ByteArrayLengthHeaderSerializer());
    return serverFactory;
  }

  @Bean
  public TcpInboundGateway tcpInboundGateway() {
    TcpInboundGateway gateway = new TcpInboundGateway();
    gateway.setConnectionFactory(serverConnectionFactory());
    gateway.setRequestChannel(requestChannel());
    return gateway;
  }

  @Bean
  public IntegrationFlow serverFlow() {
    return IntegrationFlow.from(requestChannel())
        .handle(serverResponseHandler())
        .get();
  }
  @Bean
  public MessageHandler serverResponseHandler() {
    return new ServerResponseHandler();
  }

  @Bean
  public MessageChannel requestChannel() {
    return new DirectChannel();
  }

  @Bean
  public MessageChannel responseChannel() {
    return new DirectChannel();
  }

}