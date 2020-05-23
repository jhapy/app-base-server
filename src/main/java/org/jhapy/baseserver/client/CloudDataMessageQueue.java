package org.jhapy.baseserver.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.jhapy.dto.domain.notification.CloudDataMessage;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-03
 */
@Component
public class CloudDataMessageQueue {

  @Autowired
  JmsTemplate jmsTemplate;

  public void sendMessage(final CloudDataMessage cloudDataMessage) {
    jmsTemplate.send("cloudData", session -> session.createObjectMessage(cloudDataMessage));
  }
}
