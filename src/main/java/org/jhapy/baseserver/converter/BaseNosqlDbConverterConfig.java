package org.jhapy.baseserver.converter;

import org.jhapy.dto.domain.BaseEntity;
import org.mapstruct.*;

import java.util.Map;

@MapperConfig(
    mappingInheritanceStrategy = MappingInheritanceStrategy.AUTO_INHERIT_FROM_CONFIG,
    builder = @Builder(disableBuilder = true))
public interface BaseNosqlDbConverterConfig {

  @Mapping(target = "isNew", ignore = true)
  BaseEntity asDTO(
      org.jhapy.baseserver.domain.nosqldb.BaseEntity domain, @Context Map<String, Object> context);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "modifiedBy", ignore = true)
  org.jhapy.baseserver.domain.nosqldb.BaseEntity asEntity(
      BaseEntity domain, @Context Map<String, Object> context);
}