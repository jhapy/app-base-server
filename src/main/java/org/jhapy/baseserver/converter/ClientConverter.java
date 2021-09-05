package org.jhapy.baseserver.converter;

import org.jhapy.baseserver.domain.relationaldb.Client;
import org.jhapy.dto.domain.ClientDTO;
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
public abstract class ClientConverter extends GenericMapper<Client, ClientDTO> {

  @AfterMapping
  public void afterConvert(
      ClientDTO dto, @MappingTarget Client domain, @Context Map<String, Object> context) {}

  @AfterMapping
  public void afterConvert(
      Client domain, @MappingTarget ClientDTO dto, @Context Map<String, Object> context) {}
}