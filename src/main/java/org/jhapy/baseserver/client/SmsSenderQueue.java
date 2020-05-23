package org.jhapy.baseserver.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.jhapy.dto.domain.notification.Sms;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-02
 */
@Component
public class SmsSenderQueue {

  @Autowired
  private JmsTemplate jmsTemplate;

  public void sendMessage(final Sms smsMessage) {
    jmsTemplate.send("sms", session -> session.createObjectMessage(smsMessage));
  }
}
