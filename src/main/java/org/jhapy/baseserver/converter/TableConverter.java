package org.jhapy.baseserver.converter;

import org.jhapy.baseserver.domain.relationaldb.DbTable;
import org.jhapy.dto.domain.DbTableDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Mapper(
    config = BaseRelationalDbConverterConfig.class,
    componentModel = "spring",
    uses = RelationalDbReferenceMapper.class)
public abstract class TableConverter extends GenericMapper<DbTable, DbTableDTO> {

  @AfterMapping
  public void afterConvert(
      DbTableDTO dto, @MappingTarget DbTable domain, @Context Map<String, Object> context) {}

  @AfterMapping
  public void afterConvert(
      DbTable domain, @MappingTarget DbTableDTO dto, @Context Map<String, Object> context) {}
}