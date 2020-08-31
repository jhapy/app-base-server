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

package org.jhapy.baseserver.listener;

import org.jhapy.commons.utils.HasLogger;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-05-24
 */
@Component
public class TransactionListener implements HasLogger {

  //@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void beforeCommit(ApplicationEvent event) {
    String loggerPrefix = getLoggerPrefix("beforeCommit");
    logger().debug(loggerPrefix + "Source = " + event.getSource());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void afterCommit(ApplicationEvent event) {
    String loggerPrefix = getLoggerPrefix("afterCommit");
    //logger().debug(loggerPrefix + "Source = " + event.getSource());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
  public void afterRollback(ApplicationEvent event) {
    String loggerPrefix = getLoggerPrefix("afterRollback");
    logger().debug(loggerPrefix + "Source = " + event.getSource());
  }
}
