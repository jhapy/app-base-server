package org.jhapy.baseserver.domain.graphdb;

import lombok.Data;

/**
 * Base class for all translations
 *
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-03-12
 */
@Data
public class EntityTranslation  {
  private String iso3Language;
  private Boolean isTranslated = Boolean.FALSE;
  private Boolean isDefault = Boolean.FALSE;
  private String  value;
}
