package org.jhapy.baseserver.utils;

import lombok.Data;
import org.jhapy.dto.utils.LatLng;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Component
@RequestScope
@Data
public class SessionData {
  private String username;
  private UUID userId;
  private String sessionId;
  private String iso3Language;
  private LatLng currentPosition;
  private UUID externalClientID;
  private String mailbox;
  private String mailboxFullName;
}