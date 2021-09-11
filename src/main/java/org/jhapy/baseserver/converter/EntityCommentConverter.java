package org.jhapy.baseserver.converter;

import org.jhapy.baseserver.domain.relationaldb.EntityComment;
import org.jhapy.dto.domain.EntityCommentDTO;
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
    uses = {RelationalDbReferenceMapper.class})
public abstract class EntityCommentConverter
    extends GenericMapper<EntityComment, EntityCommentDTO> {

  @Override
  public abstract EntityCommentDTO asDTO(
      EntityComment entity, @Context Map<String, Object> context);

  @AfterMapping
  protected void afterConvert(
      EntityCommentDTO dto,
      @MappingTarget EntityComment domain,
      @Context Map<String, Object> context) {}

  @AfterMapping
  protected void afterConvert(
      EntityComment domain,
      @MappingTarget EntityCommentDTO dto,
      @Context Map<String, Object> context) {}
}