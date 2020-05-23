package org.jhapy.baseserver.domain.relationaldb;

import javax.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Base class for all translations
 *
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-09-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@MappedSuperclass
public abstract class EntityTranslation extends BaseEntity {

  /**
   * Is default translation
   */
  private Boolean isDefault;

  /**
   * Language
   */
  private String iso3Language;

  private Boolean isTranslated;
}