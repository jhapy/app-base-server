package org.jhapy.baseserver.converter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Context;
import org.mapstruct.Mapping;

public abstract class GenericMapper<ENTITY, DTO> extends BaseConverterV2 {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "modifiedBy", ignore = true)
  public abstract ENTITY asEntity(DTO dto, @Context Map<String, Object> context);

  public abstract DTO asDTO(ENTITY entity, @Context Map<String, Object> context);

  public abstract List<ENTITY> asEntityList(Iterable<DTO> dtoList, @Context Map<String, Object> context);

  public abstract List<DTO> asDTOList(Iterable<ENTITY> entityList, @Context Map<String, Object> context);

  public List<String> map( String value ) {
    if (StringUtils.isNotBlank(value))
      return Arrays.asList(value.split(","));
    else return null;
  }

  public String map( List<String> values ) {
    if ( values != null && ! values.isEmpty() )
      return String.join(",", values);
    else
      return null;
  }
}