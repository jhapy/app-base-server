package org.jhapy.baseserver.service;

import org.jhapy.dto.domain.i18n.*;
import org.jhapy.dto.domain.i18n.ActionDTO;
import org.jhapy.dto.messageQueue.I18NUpdateTypeEnum;

/**
 * @author Alexandre Clavaud.
 * @version 1.0
 * @since 27/03/2021
 */
public interface I18NService {

  String getElement(String name, String iso3Lang);

  String getAction(String name, String iso3Lang);

  String getMessage(String name, String iso3Lang, String... params);

  void elementUpdate(I18NUpdateTypeEnum updateType, ElementDTO element);

  void elementTrlUpdate(I18NUpdateTypeEnum updateType, ElementTrlDTO elementTrl);

  void actionUpdate(I18NUpdateTypeEnum updateType, ActionDTO action);

  void actionTrlUpdate(I18NUpdateTypeEnum updateType, ActionTrlDTO actionTrl);

  void messageUpdate(I18NUpdateTypeEnum updateType, MessageDTO message);

  void messageTrlUpdate(I18NUpdateTypeEnum updateType, MessageTrlDTO messageTrl);
}