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

package org.jhapy.baseserver.converter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.jhapy.dto.domain.security.SecurityKeycloakGroup;
import org.jhapy.dto.domain.security.SecurityKeycloakRole;
import org.jhapy.dto.domain.security.SecurityKeycloakUser;
import org.jhapy.dto.utils.StoredFile;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 12/06/2020
 */
@Mapper(componentModel = "spring")
public abstract class SecurityConverter {

  public abstract SecurityKeycloakGroup convertToDto(GroupRepresentation domain);

  public abstract GroupRepresentation convertToDomain(SecurityKeycloakGroup dto);

  public abstract List<SecurityKeycloakGroup> convertToDtoSecurityKeycloakGroups(
      Iterable<GroupRepresentation> domains);

  public abstract List<GroupRepresentation> convertToDomainGroupRepresentations(
      Iterable<SecurityKeycloakGroup> dtos);

  public abstract SecurityKeycloakRole convertToDto(RoleRepresentation domain);

  public abstract RoleRepresentation convertToDomain(SecurityKeycloakRole dto);

  public abstract List<SecurityKeycloakRole> convertToDtoSecurityKeycloakRoles(
      Iterable<RoleRepresentation> domains);

  public abstract List<RoleRepresentation> convertToDomainRoleRepresentations(
      Iterable<SecurityKeycloakRole> dtos);

  @Mapping(target = "groups", ignore = true)
  @Mapping(target = "attributes", ignore = true)
  public abstract SecurityKeycloakUser convertToDto(UserRepresentation domain);

  @Mapping(target = "groups", ignore = true)
  @Mapping(target = "attributes", ignore = true)
  public abstract UserRepresentation convertToDomain(SecurityKeycloakUser dto);

  public abstract List<SecurityKeycloakUser> convertToDtoSecurityKeycloakUsers(
      Iterable<UserRepresentation> domains);

  public abstract List<UserRepresentation> convertToDomainUserRepresentations(
      Iterable<SecurityKeycloakUser> dtos);

  @AfterMapping
  protected void afterConvert(SecurityKeycloakUser dto,
      @MappingTarget UserRepresentation domain,
      @Context Map<String, Object> context) {
    domain.setEnabled(dto.getIsActivated());
    domain.getAttributes().forEach(
        (s, o) -> dto.getAttributes().put(s, Collections.singletonList(o.toString())));
  }

  @AfterMapping
  protected void afterConvert(UserRepresentation domain,
      @MappingTarget SecurityKeycloakUser dto,
      @Context Map<String, Object> context) {
    if (domain.getAttributes() != null && domain.getAttributes().get("title") != null) {
      dto.setTitle(domain.getAttributes().get("title").get(0));
    }
    if (domain.getAttributes() != null && domain.getAttributes().get("phone") != null) {
      dto.setMobileNumber(domain.getAttributes().get("phone").get(0));
    }
    if (domain.getAttributes() != null && domain.getAttributes().get("locale") != null) {
      dto.setLocale(domain.getAttributes().get("locale").get(0));
    }
    dto.setIsLocal(domain.getFederationLink() != null || (domain.getSocialLinks() != null
        && domain.getSocialLinks().size() > 0));
    if (domain.getAttributes() != null && domain.getAttributes().get("picture") != null) {
      String pictureStr = domain.getAttributes().get("picture").get(0);
      if (pictureStr.startsWith("http")) {
        dto.getAttributes().put("picture", pictureStr);
      } else {
        byte[] pictureDecoded = java.util.Base64.getDecoder().decode(pictureStr);
        try {
          MimeType mimeType = TikaConfig.getDefaultConfig().getMimeRepository()
              .forName((new Tika()).detect(pictureDecoded));
          String fileExt = mimeType.getExtension();

          StoredFile storedFile = new StoredFile();
          storedFile.setContent(pictureDecoded);
          storedFile.setOrginalContent(pictureDecoded);
          storedFile.setId(UUID.randomUUID().toString());
          storedFile.setFilename(storedFile.getId() + fileExt);
          storedFile.setFilesize((long) pictureDecoded.length);
          storedFile.setMimeType(mimeType.toString());
          dto.setPicture(storedFile);
        } catch (MimeTypeException e) {
          e.printStackTrace();
        }
      }
    }
    Objects.requireNonNull(domain.getAttributes())
        .keySet().stream()
        .filter(s -> !s.equalsIgnoreCase("title") && !s.equalsIgnoreCase("phone") &&
            !s.equalsIgnoreCase("locale") && !s.equalsIgnoreCase("picture"))
        .forEach(s -> dto.getAttributes().put(s, domain.getAttributes().get(s).get(0)));

    dto.setIsActivated(domain.isEnabled());
  }
}
