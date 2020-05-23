package org.jhapy.baseserver.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.jhapy.dto.domain.notification.Mail;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-02
 */
@Component
public class EmailSenderQueue {

  @Autowired
  JmsTemplate jmsTemplate;

  public void sendMessage(final Mail mailMessage) {
    jmsTemplate.send("mailbox", session -> session.createObjectMessage(mailMessage));
  }
}
