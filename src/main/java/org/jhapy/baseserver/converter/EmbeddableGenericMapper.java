package org.jhapy.baseserver.converter;

import org.mapstruct.Context;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;

public abstract class EmbeddableGenericMapper<E, D> extends BaseConverterV2 {

  @Mapping(target = "created", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "modifiedBy", ignore = true)
  public abstract E asEntity(D dto, @Context Map<String, Object> context);

  @Mapping(target = "isNew", ignore = true)
  @Mapping(target = "id", ignore = true)
  public abstract D asDTO(E entity, @Context Map<String, Object> context);

  public abstract List<E> asEntityList(Iterable<D> dtoList, @Context Map<String, Object> context);

  public abstract List<D> asDTOList(Iterable<E> entityList, @Context Map<String, Object> context);
}