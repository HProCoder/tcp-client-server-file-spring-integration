package com.hprocoder.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.ip.tcp.TcpOutboundGateway;
import org.springframework.integration.ip.tcp.connection.TcpConnectionCloseEvent;
import org.springframework.integration.ip.tcp.connection.TcpConnectionExceptionEvent;
import org.springframework.integration.ip.tcp.connection.TcpConnectionOpenEvent;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLengthHeaderSerializer;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
public class ClientConfig {

  private static final Logger LOGGER = LogManager.getLogger(ClientConfig.class);
  private static final String SERVER_HOST = "localhost";
  private static final int SERVER_PORT = 12345;

  @Bean
  public TcpNetClientConnectionFactory tcpNetClientConnectionFactory() {
    TcpNetClientConnectionFactory clientFactory = new TcpNetClientConnectionFactory(SERVER_HOST,
        SERVER_PORT);
    clientFactory.setSerializer(new ByteArrayLengthHeaderSerializer());
    clientFactory.setDeserializer(new ByteArrayLengthHeaderSerializer());
    clientFactory.setConnectTimeout(3000);
    clientFactory.setSoTimeout(10000);
    clientFactory.setSingleUse(true);
    return clientFactory;
  }

  @Bean
  public MessageChannel toChannel() {
    return new DirectChannel();
  }

  @Bean
  public MessageChannel fromChannel() {
    return new DirectChannel();
  }

  @Bean
  public TcpOutboundGateway tcpOutboundGateway() {
    TcpOutboundGateway tcpOutboundGateway = new TcpOutboundGateway();
    tcpOutboundGateway.setConnectionFactory(tcpNetClientConnectionFactory());
    tcpOutboundGateway.setLoggingEnabled(true);
    return tcpOutboundGateway;
  }

  @Bean
  public IntegrationFlow requestFlow(TcpOutboundGateway tcpOutboundGateway) {
    return IntegrationFlow.from(toChannel())
        .handle(tcpOutboundGateway)
        .channel(fromChannel())
        .get();
  }

  @Bean
  public IntegrationFlow responseFlow(GenericHandler<byte[]> messageProcessor) {
    return IntegrationFlow.from(fromChannel())
        .handle(messageProcessor)
        .get();
  }

  @Bean
  public GenericHandler<byte[]> genericHandler(){
    return new MessageProcessor();
  }

  @EventListener
  public void handleTcpConnectionOpened(TcpConnectionOpenEvent event) {
    LOGGER.info("Connection opened with connectionId : {}", event.getConnectionId());
  }

  @EventListener
  public void handleTcpConnectionClosed(TcpConnectionCloseEvent event) {
    LOGGER.info("Connection closed with connectionId : {}", event.getConnectionId());
  }

  @EventListener
  public void handleTcpConnectionException(TcpConnectionExceptionEvent event) {
    LOGGER.info("Exception : {} for connectionId {}", event.getCause(), event.getConnectionId());
  }
}
