package org.jhapy.baseserver.converter;

import java.lang.reflect.InvocationTargetException;
import javax.persistence.EntityManager;
import org.dom4j.tree.AbstractEntity;
import org.jhapy.baseserver.domain.relationaldb.BaseEntity;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.commons.utils.HasLoggerStatic;
import org.jhapy.dto.domain.BaseEntityLongId;
import org.mapstruct.ObjectFactory;
import org.mapstruct.TargetType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty("spring.datasource.url")
public class RelationalDbReferenceMapper {

  private final EntityManager em;

  public RelationalDbReferenceMapper(EntityManager em) {
    this.em = em;
  }

  @ObjectFactory
  public <T extends BaseEntity> T resolve(BaseEntityLongId sourceDTO,
      @TargetType Class<T> type) {
    String loggerPrefix = HasLoggerStatic.getLoggerPrefix("resolve");
    T entity = null;
    if (sourceDTO.getId() != null) {
      entity = em.find(type, sourceDTO.getId());
    }
    try {
      if (entity == null) {
        entity = type.getDeclaredConstructor().newInstance();
      }
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      HasLoggerStatic.error(RelationalDbReferenceMapper.class, loggerPrefix, "Unexpected error : "+ e.getMessage(), e);
    }
    return entity;
  }
}