package org.jhapy.baseserver.converter;

import org.jhapy.dto.domain.BaseEntity;
import org.jhapy.dto.domain.EntityTranslationV2;
import org.mapstruct.*;

import java.util.Map;

@MapperConfig(
    mappingInheritanceStrategy = MappingInheritanceStrategy.AUTO_INHERIT_FROM_CONFIG,
    builder = @Builder(disableBuilder = true))
public interface BaseRelationalDbConverterConfig {

  @Mapping(target = "clientName", ignore = true)
  BaseEntity asDTO(
      org.jhapy.baseserver.domain.relationaldb.BaseEntity domain,
      @Context Map<String, Object> context);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "modifiedBy", ignore = true)
  org.jhapy.baseserver.domain.relationaldb.BaseEntity asEntity(
      BaseEntity domain, @Context Map<String, Object> context);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "new", ignore = true)
  EntityTranslationV2 asDTO(
      org.jhapy.baseserver.domain.relationaldb.EntityTranslationV2 domain,
      @Context Map<String, Object> context);

  @Mapping(target = "created", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "modifiedBy", ignore = true)
  org.jhapy.baseserver.domain.relationaldb.EntityTranslationV2 asEntity(
      EntityTranslationV2 dto, @Context Map<String, Object> context);
}