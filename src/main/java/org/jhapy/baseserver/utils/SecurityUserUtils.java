package org.jhapy.baseserver.utils;

import java.util.List;
import org.springframework.stereotype.Component;
import org.jhapy.baseserver.client.SecurityUserService;
import org.jhapy.dto.domain.security.SecurityRole;
import org.jhapy.dto.domain.security.SecurityUser;
import org.jhapy.dto.serviceQuery.ServiceResult;
import org.jhapy.dto.serviceQuery.generic.GetByStrIdQuery;
import org.jhapy.dto.utils.AppContextThread;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 05/04/2020
 */
@Component
public class SecurityUserUtils {
private static ThreadLocal<SecurityUser> currentSecurityUser = new ThreadLocal<>();

private final SecurityUserService securityUserService;

  public SecurityUserUtils(
      SecurityUserService securityUserService) {
    this.securityUserService = securityUserService;
  }

  public SecurityUser getCurrentSecurityUser() {
    String currentSecurityUserId = AppContextThread.getCurrentSecurityUserId();
    ServiceResult<SecurityUser> securityUserServiceResult = securityUserService.getById( new GetByStrIdQuery( currentSecurityUserId ));
    if ( securityUserServiceResult.getIsSuccess() && securityUserServiceResult.getData() != null ) {
        return securityUserServiceResult.getData();
    } else {
      return null;
    }
}

public boolean hasRole( String securityRole ) {
    if (  getCurrentSecurityUser() != null ) {
      SecurityUser securityUser = getCurrentSecurityUser();
      for (SecurityRole securityRole1 : securityUser.getRoles()) {
        if ( securityRole1.getName().equalsIgnoreCase( securityRole))
          return true;
      }
    }
    return false;
}
}
