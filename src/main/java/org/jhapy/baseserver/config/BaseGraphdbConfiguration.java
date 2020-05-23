package org.jhapy.baseserver.config;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.jhapy.commons.utils.HasLogger;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-04-01
 */
//@Configuration
//@EnableNeo4jAuditing(auditorAwareRef = "springSecurityAuditorAware")
//@EnableNeo4jRepositories(basePackages = "org.jhapy.backend.repository.graphdb", transactionManagerRef = "graphdbTransactionManager")
//@EntityScan("org.jhapy.backend.domain.graphdb")
//@EnableTransactionManagement
public class BaseGraphdbConfiguration implements HasLogger {

  @Bean(name = "graphdbTransactionManager")
  public Neo4jTransactionManager transactionManager(SessionFactory sessionFactory) {
    return new Neo4jTransactionManager(sessionFactory);
  }

  @Bean
  public Session getSession( SessionFactory sessionFactory ) {
    return sessionFactory.openSession();
  }
}
