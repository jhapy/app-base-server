package org.jhapy.baseserver.utils;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jhapy.dto.utils.LatLng;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@Data
public class SessionData {
  private String username;
  private String userId;
  private String sessionId;
  private String iso3Language;
  private LatLng currentPosition;
  private Long externalClientID;
  private String mailbox;
  private String mailboxFullName;
}