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

package org.jhapy.baseserver.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.jhapy.baseserver.utils.SessionData;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.commons.utils.SpringApplicationContext;
import org.jhapy.dto.serviceQuery.BaseRemoteQuery;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging execution of service and repository Spring components.
 *
 * <p>By default, it only runs with the "dev" profile.
 */
@Aspect
@Component
public class SessionDataAspect implements HasLogger {
  public SessionDataAspect() {
    debug(getLoggerPrefix("sessionDataAspect"), "Init");
  }

  @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
  public void restControllerPointcut() {}

  @Around("restControllerPointcut()")
  public Object restControllerPointcut(ProceedingJoinPoint joinPoint) throws Throwable {
    Object[] args = joinPoint.getArgs();
    if (args.length == 1 && args[0] instanceof BaseRemoteQuery) {
      var query = (BaseRemoteQuery) args[0];
      SessionData sessionDataFromContext = SpringApplicationContext.getBean(SessionData.class);
      sessionDataFromContext.setSessionId(query.getQuerySessionId());
      sessionDataFromContext.setUserId(query.getQueryUserId());
      sessionDataFromContext.setSessionId(query.getQuerySessionId());
      sessionDataFromContext.setIso3Language(query.getQueryIso3Language());
      sessionDataFromContext.setCurrentPosition(query.getQueryCurrentPosition());
      sessionDataFromContext.setExternalClientID(query.getQueryExternalClientID());
      sessionDataFromContext.setMailbox(query.getQueryMailbox());
      sessionDataFromContext.setMailboxFullName(query.getQueryMailboxFullName());
    }
    return joinPoint.proceed();
  }
}