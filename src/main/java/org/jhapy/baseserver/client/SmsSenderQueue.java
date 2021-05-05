/*
 * Copyright 2020-2020 the original author or authors from the JHapy project.
 *
 * This file is part of the JHapy project, see https://www.jhapy.org/ for more information.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jhapy.baseserver.client;

import org.jhapy.dto.domain.notification.Sms;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-02
 */
@Component
public class SmsSenderQueue {

  private final AmqpTemplate amqpTemplate;
  private final Queue smsQueue;

  public SmsSenderQueue(AmqpTemplate amqpTemplate,
      @Qualifier("smsQueue") Queue smsQueue) {
    this.amqpTemplate = amqpTemplate;
    this.smsQueue = smsQueue;
  }

  public void sendMessage(final Sms smsMessage) {
    amqpTemplate.convertAndSend(smsQueue.getName(), smsMessage);
  }
}
