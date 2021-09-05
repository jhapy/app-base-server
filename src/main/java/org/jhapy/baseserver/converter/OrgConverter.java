package org.jhapy.baseserver.converter;

import org.jhapy.baseserver.domain.relationaldb.Org;
import org.jhapy.dto.domain.OrgDTO;
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
public abstract class OrgConverter extends GenericMapper<Org, OrgDTO> {

  @AfterMapping
  public void afterConvert(
      OrgDTO dto, @MappingTarget Org domain, @Context Map<String, Object> context) {}

  @AfterMapping
  public void afterConvert(
      Org domain, @MappingTarget OrgDTO dto, @Context Map<String, Object> context) {}
}