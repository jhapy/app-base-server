package org.jhapy.baseserver.converter;

import java.util.Collection;
import java.util.List;
import org.jhapy.commons.converter.CommonsConverterV2;
import org.jhapy.dto.domain.BaseEntity;
import org.jhapy.dto.domain.Comment;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * @author Alexandre Clavaud.
 * @version 1.0
 * @since 18/05/2021
 */
@Mapper(componentModel = "spring")
public abstract class BaseConverterV2 extends CommonsConverterV2 {

  public abstract Comment convertToDto(org.jhapy.baseserver.domain.graphdb.Comment domain);

  public abstract org.jhapy.baseserver.domain.graphdb.Comment convertToDomain(Comment dto);

  public abstract List<Comment> convertToDtoComments(
      Collection<org.jhapy.baseserver.domain.graphdb.Comment> domains);

  public abstract List<org.jhapy.baseserver.domain.graphdb.Comment> convertToDomainComments(
      Collection<Comment> dtos);

  @AfterMapping
  protected void afterConvert(BaseEntity dto,
      @MappingTarget org.jhapy.baseserver.domain.graphdb.BaseEntity domain) {
    if (dto.getIsNew()) {
      domain.setId(null);
    }
  }

  @AfterMapping
  protected void afterConvert(BaseEntity dto,
      @MappingTarget org.jhapy.baseserver.domain.nosqldb.BaseEntity domain) {
    if (dto.getIsNew()) {
      domain.setId(null);
    }
  }

  @AfterMapping
  protected void afterConvert(BaseEntity dto,
      @MappingTarget org.jhapy.baseserver.domain.relationaldb.BaseEntity domain) {
    if (dto.getIsNew()) {
      domain.setId(null);
    }
  }
}
