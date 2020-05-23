package org.jhapy.baseserver.listener;

import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.jhapy.commons.utils.HasLogger;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-05-24
 */
@Component
public class TransactionListener implements HasLogger {

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void beforeCommit(ApplicationEvent event) {
    String loggerPrefix = getLoggerPrefix("beforeCommit");
    logger().debug(loggerPrefix + "Source = " + event.getSource());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void afterCommit(ApplicationEvent event) {
    String loggerPrefix = getLoggerPrefix("afterCommit");
    logger().debug(loggerPrefix + "Source = " + event.getSource());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
  public void afterRollback(ApplicationEvent event) {
    String loggerPrefix = getLoggerPrefix("afterRollback");
    logger().debug(loggerPrefix + "Source = " + event.getSource());
  }
}
