package org.jhapy.baseserver.receiver;

import org.jhapy.baseserver.service.ClientService;
import org.jhapy.baseserver.service.I18NService;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.dto.messageQueue.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-03
 */
@Component
public class ClientReceiver implements HasLogger {

  private final ClientService clientService;

  public ClientReceiver(ClientService clientService) {
    this.clientService = clientService;
  }

  @RabbitListener(queues = "#{clientUpdateQueue.name}")
  public void onClientUpdate(final ClientUpdate update) {
    var loggerPrefix = getLoggerPrefix("onClientUpdate", update);

    logger().info(loggerPrefix + "Message received");

    clientService.clientUpdate(update.getUpdateType(), update.getClientDTO());
  }
}