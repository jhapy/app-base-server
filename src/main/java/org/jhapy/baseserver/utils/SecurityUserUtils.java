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

package org.jhapy.baseserver.utils;

import org.jhapy.baseserver.client.SecurityUserService;
import org.jhapy.dto.domain.security.SecurityRole;
import org.jhapy.dto.domain.security.SecurityUser;
import org.jhapy.dto.serviceQuery.ServiceResult;
import org.jhapy.dto.serviceQuery.generic.GetByStrIdQuery;
import org.jhapy.dto.utils.AppContextThread;
import org.springframework.stereotype.Component;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 05/04/2020
 */
@Component
public class SecurityUserUtils {

  private static final ThreadLocal<SecurityUser> currentSecurityUser = new ThreadLocal<>();

  private final SecurityUserService securityUserService;

  public SecurityUserUtils(
      SecurityUserService securityUserService) {
    this.securityUserService = securityUserService;
  }

  public SecurityUser getCurrentSecurityUser() {
    String currentSecurityUserId = AppContextThread.getCurrentSecurityUserId();
    ServiceResult<SecurityUser> securityUserServiceResult = securityUserService
        .getById(new GetByStrIdQuery(currentSecurityUserId));
    if (securityUserServiceResult.getIsSuccess() && securityUserServiceResult.getData() != null) {
      return securityUserServiceResult.getData();
    } else {
      return null;
    }
  }

  public boolean hasRole(String securityRole) {
    if (getCurrentSecurityUser() != null) {
      SecurityUser securityUser = getCurrentSecurityUser();
      for (SecurityRole securityRole1 : securityUser.getRoles()) {
        if (securityRole1.getName().equalsIgnoreCase(securityRole)) {
          return true;
        }
      }
    }
    return false;
  }
}
