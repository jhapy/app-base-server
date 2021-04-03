package org.jhapy.baseserver.service;

import org.jhapy.dto.domain.i18n.Action;
import org.jhapy.dto.domain.i18n.ActionTrl;
import org.jhapy.dto.domain.i18n.Element;
import org.jhapy.dto.domain.i18n.ElementTrl;
import org.jhapy.dto.domain.i18n.Message;
import org.jhapy.dto.domain.i18n.MessageTrl;
import org.jhapy.dto.messageQueue.I18NUpdateTypeEnum;

/**
 * @author Alexandre Clavaud.
 * @version 1.0
 * @since 27/03/2021
 */
public interface I18NService {
  String getElement( String name, String iso3Lang);

  String getAction( String name, String iso3Lang);

  String getMessage( String name, String iso3Lang, String ... params);

  void elementUpdate(I18NUpdateTypeEnum updateType, Element element);

  void elementTrlUpdate(I18NUpdateTypeEnum updateType, ElementTrl elementTrl);

  void actionUpdate(I18NUpdateTypeEnum updateType, Action action);

  void actionTrlUpdate(I18NUpdateTypeEnum updateType, ActionTrl actionTrl);

  void messageUpdate(I18NUpdateTypeEnum updateType, Message message);

  void messageTrlUpdate(I18NUpdateTypeEnum updateType, MessageTrl messageTrl);
}
