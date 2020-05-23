package org.jhapy.baseserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 10/21/19
 */
//@Configuration
public class ChainedTransactionConfiguration {

  private MongoTransactionManager nosqlTransactionManager;
  private JpaTransactionManager relationaldbTransactionManager;
  private Neo4jTransactionManager graphdbTransactionManager;

  public ChainedTransactionConfiguration(Neo4jTransactionManager graphdbTransactionManager,
      MongoTransactionManager nosqlTransactionManager,
      JpaTransactionManager relationaldbTransactionManager) {
    this.graphdbTransactionManager = graphdbTransactionManager;
    this.nosqlTransactionManager = nosqlTransactionManager;
    this.relationaldbTransactionManager = relationaldbTransactionManager;
  }

  @Primary
  @Bean("transactionManager")
  public PlatformTransactionManager getTransactionManager() {
    ChainedTransactionManager transactionManager = new ChainedTransactionManager(
        graphdbTransactionManager, relationaldbTransactionManager, nosqlTransactionManager);
    return transactionManager;
  }
}
