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

import org.neo4j.springframework.data.core.transaction.Neo4jTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoTransactionManager;
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

  private final MongoTransactionManager nosqlTransactionManager;
  private final JpaTransactionManager relationaldbTransactionManager;
  private final Neo4jTransactionManager graphdbTransactionManager;

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
