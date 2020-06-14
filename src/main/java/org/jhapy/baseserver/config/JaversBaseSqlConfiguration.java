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

import javax.servlet.http.HttpServletRequest;
import org.javers.spring.auditable.AuthorProvider;
import org.jhapy.commons.security.SecurityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 20/04/2020
 */
@Configuration
public class JaversBaseSqlConfiguration {

  @Bean
  public AuthorProvider authorProvider() {
    return () -> {
      String currentUsername = "Unknown";
      try {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
          HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes)
              .getRequest();
          currentUsername = servletRequest.getHeader("X-SecUsername");
        } else {
          currentUsername = SecurityUtils.getUsername();
        }
      } catch (IllegalStateException e) {
      }
      return currentUsername;
    };
  }
}
