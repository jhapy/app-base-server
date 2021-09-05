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

package org.jhapy.baseserver.listener.relationaldb;

import org.jhapy.baseserver.domain.relationaldb.Client;
import org.jhapy.baseserver.service.ClientService;
import org.jhapy.commons.utils.SpringApplicationContext;
import org.springframework.stereotype.Component;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2/10/20
 */
@Component
public class ClientListener {

  private ClientService clientService;

  @PostPersist
  public void postPersist(Client client) {
    if (getClientService() != null) {
      getClientService().postPersist(client);
    }
  }

  @PostUpdate
  public void postUpdate(Client client) {
    if (getClientService() != null) {
      getClientService().postUpdate(client);
    }
  }

  @PostRemove
  public void postRemove(Client client) {
    if (getClientService() != null) {
      getClientService().postRemove(client);
    }
  }

  protected ClientService getClientService() {
    if (clientService == null) {
      clientService = SpringApplicationContext.getBean(ClientService.class);
    }
    return clientService;
  }
}