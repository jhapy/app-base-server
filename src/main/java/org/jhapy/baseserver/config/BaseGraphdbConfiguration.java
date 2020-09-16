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

package org.jhapy.baseserver.config;

import org.jhapy.commons.utils.HasLogger;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;

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
  public Session getSession(SessionFactory sessionFactory) {
    return sessionFactory.openSession();
  }
}
