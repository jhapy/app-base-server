package org.jhapy.baseserver.converter;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.jhapy.baseserver.domain.graphdb.Comment;
import org.jhapy.commons.utils.OrikaBeanMapper;
import org.jhapy.dto.domain.BaseEntity;
import org.jhapy.dto.domain.EntityTranslation;
import org.jhapy.dto.utils.Page;
import org.jhapy.dto.utils.StoredFile;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-06-05
 */
@Component
public class BaseConverter {

  private final OrikaBeanMapper orikaBeanMapper;

  public BaseConverter(OrikaBeanMapper orikaBeanMapper) {
    this.orikaBeanMapper = orikaBeanMapper;
  }

  @Bean
  public void baseConverters() {
    orikaBeanMapper.addMapper(Comment.class, org.jhapy.dto.domain.Comment.class);
    orikaBeanMapper.addMapper(org.jhapy.dto.domain.Comment.class, Comment.class);

    orikaBeanMapper
        .addMapper(EntityTranslation.class, EntityTranslation.class);
    orikaBeanMapper
        .addMapper(EntityTranslation.class, EntityTranslation.class);

    orikaBeanMapper
        .addMapper(org.jhapy.baseserver.domain.graphdb.BaseEntity.class,
            BaseEntity.class);
    orikaBeanMapper
        .addMapper(org.jhapy.baseserver.domain.nosqldb.BaseEntity.class,
            BaseEntity.class);
    orikaBeanMapper
        .addMapper(org.jhapy.baseserver.domain.relationaldb.BaseEntity.class, BaseEntity.class);

    orikaBeanMapper
        .getClassMapBuilder(BaseEntity.class, org.jhapy.baseserver.domain.graphdb.BaseEntity.class)
        .byDefault().customize(
        new CustomMapper<BaseEntity, org.jhapy.baseserver.domain.graphdb.BaseEntity>() {
          @Override
          public void mapAtoB(BaseEntity a, org.jhapy.baseserver.domain.graphdb.BaseEntity b,
              MappingContext context) {
            if (a.getIsNew()) {
              b.setId(null);
            }
          }
        }).register();

    orikaBeanMapper
        .getClassMapBuilder(BaseEntity.class, org.jhapy.baseserver.domain.nosqldb.BaseEntity.class)
        .byDefault().customize(
        new CustomMapper<BaseEntity, org.jhapy.baseserver.domain.nosqldb.BaseEntity>() {
          @Override
          public void mapAtoB(BaseEntity a,
              org.jhapy.baseserver.domain.nosqldb.BaseEntity b,
              MappingContext context) {
            if (a.getIsNew()) {
              b.setId(null);
            }
          }
        }).register();

    orikaBeanMapper
        .getClassMapBuilder(BaseEntity.class,
            org.jhapy.baseserver.domain.relationaldb.BaseEntity.class)
        .byDefault().customize(
        new CustomMapper<BaseEntity, org.jhapy.baseserver.domain.relationaldb.BaseEntity>() {
          @Override
          public void mapAtoB(BaseEntity a,
              org.jhapy.baseserver.domain.relationaldb.BaseEntity b,
              MappingContext context) {
            if (a.getIsNew()) {
              b.setId(null);
            }
          }
        }).register();

    orikaBeanMapper.addMapper(PageImpl.class, Page.class);
    orikaBeanMapper.addMapper(Page.class, PageImpl.class);

    orikaBeanMapper.addMapper(StoredFile.class, StoredFile.class);
  }
}