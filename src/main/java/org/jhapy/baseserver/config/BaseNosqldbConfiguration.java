package org.jhapy.baseserver.config;


import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.jhapy.commons.utils.HasLogger;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-02-14
 */
// @Configuration
// @Import(value = MongoAutoConfiguration.class)
// @EnableMongoAuditing(auditorAwareRef = "springSecurityAuditorAware")
// @EntityScan("org.jhapy.backend.domain.relationaldb")
// @EnableTransactionManagement
public class BaseNosqldbConfiguration implements HasLogger {

  @Bean(name = "nosqldbTransactionManager")
  public MongoTransactionManager transactionManager(MongoDbFactory dbFactory) {
    return new MongoTransactionManager(dbFactory);
  }

  @Bean
  public ValidatingMongoEventListener validatingMongoEventListener() {
    return new ValidatingMongoEventListener(validator());
  }

  @Bean
  public LocalValidatorFactoryBean validator() {
    return new LocalValidatorFactoryBean();
  }
}