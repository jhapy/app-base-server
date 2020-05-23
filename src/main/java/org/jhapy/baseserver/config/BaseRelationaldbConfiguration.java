package org.jhapy.baseserver.config;

import javax.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;

// @Configuration
// @EnableJpaRepositories(basePackages = "org.jhapy.backend.repository.relationaldb", transactionManagerRef = "relationaldbTransactionManager")
// @EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
// @EntityScan("org.jhapy.backend.domain.relationaldb")
// @EnableTransactionManagement
public class BaseRelationaldbConfiguration {
  @Bean(name = "relationaldbTransactionManager")
  public JpaTransactionManager relationaldbTransactionManager(EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }
}
