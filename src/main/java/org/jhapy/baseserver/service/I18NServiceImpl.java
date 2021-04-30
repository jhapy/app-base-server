package org.jhapy.baseserver.service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.jhapy.baseserver.client.i18n.ActionTrlService;
import org.jhapy.baseserver.client.i18n.ElementTrlService;
import org.jhapy.baseserver.client.i18n.MessageTrlService;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.dto.domain.i18n.Action;
import org.jhapy.dto.domain.i18n.ActionTrl;
import org.jhapy.dto.domain.i18n.Element;
import org.jhapy.dto.domain.i18n.ElementTrl;
import org.jhapy.dto.domain.i18n.Message;
import org.jhapy.dto.domain.i18n.MessageTrl;
import org.jhapy.dto.messageQueue.I18NUpdateTypeEnum;
import org.jhapy.dto.serviceQuery.ServiceResult;
import org.jhapy.dto.serviceQuery.i18n.FindByIso3Query;
import org.jhapy.dto.serviceQuery.i18n.GetByNameAndIso3Query;
import org.springframework.stereotype.Service;

/**
 * @author Alexandre Clavaud.
 * @version 1.0
 * @since 27/03/2021
 */
@Service
public class I18NServiceImpl implements I18NService, HasLogger {
  private final ElementTrlService elementTrlService;
  private final ActionTrlService actionTrlService;
  private final MessageTrlService messageTrlService;

  private Map<String, Map<String,String>> elements = new HashMap<>();
  private Map<String, Map<String,String>> actions = new HashMap<>();
  private Map<String, Map<String,String>> messsages = new HashMap<>();

  public I18NServiceImpl(org.jhapy.baseserver.client.i18n.I18NService i18NService,
      ElementTrlService elementTrlService,
      ActionTrlService actionTrlService,
      MessageTrlService messageTrlService) {
    this.elementTrlService = elementTrlService;
    this.actionTrlService = actionTrlService;
    this.messageTrlService = messageTrlService;
  }

  @Override
  public void elementUpdate(I18NUpdateTypeEnum updateType, Element element) {
  }

  @Override
  public void elementTrlUpdate(I18NUpdateTypeEnum updateType, ElementTrl elementTrl) {
    String loggerPrefix = getLoggerPrefix("elementTrlUpdate", updateType, elementTrl );

    elements.computeIfAbsent(elementTrl.getIso3Language(), k -> new HashMap<>());
    if ( updateType.equals(I18NUpdateTypeEnum.DELETE)) {
      logger().debug(loggerPrefix+"Delete record");
      elements.get(elementTrl.getIso3Language()).remove(elementTrl.getName());
    }
    else {
      logger().debug(loggerPrefix+"Create or Update record");
      elements.get(elementTrl.getIso3Language()).put(elementTrl.getName(), elementTrl.getValue());
    }
  }

  @Override
  public void actionUpdate(I18NUpdateTypeEnum updateType, Action action) {
  }

  @Override
  public void actionTrlUpdate(I18NUpdateTypeEnum updateType, ActionTrl actionTrl) {
    String loggerPrefix = getLoggerPrefix("actionTrlUpdate", updateType, actionTrl );

    actions.computeIfAbsent(actionTrl.getIso3Language(), k -> new HashMap<>());
    if ( updateType.equals(I18NUpdateTypeEnum.DELETE)) {
      logger().debug(loggerPrefix+"Delete record");
      actions.get(actionTrl.getIso3Language()).remove(actionTrl.getName());
    }
    else {
      logger().debug(loggerPrefix+"Create or Update record");
      actions.get(actionTrl.getIso3Language()).put(actionTrl.getName(), actionTrl.getValue());
    }
  }

  @Override
  public void messageUpdate(I18NUpdateTypeEnum updateType, Message message) {
  }

  @Override
  public void messageTrlUpdate(I18NUpdateTypeEnum updateType, MessageTrl messageTrl) {
    String loggerPrefix = getLoggerPrefix("messageTrlUpdate", updateType, messageTrl );

    messsages.computeIfAbsent(messageTrl.getIso3Language(), k -> new HashMap<>());
    if ( updateType.equals(I18NUpdateTypeEnum.DELETE)) {
      logger().debug(loggerPrefix+"Delete record");
      messsages.get(messageTrl.getIso3Language()).remove(messageTrl.getName());}
    else {
      logger().debug(loggerPrefix+"Create or Update record");
      messsages.get(messageTrl.getIso3Language()).put(messageTrl.getName(), messageTrl.getValue());
    }
  }

  @Override
  public String getElement(String name, String iso3Lang) {
    String loggerPrefix = getLoggerPrefix("getElement", name, iso3Lang);
    if ( elements.containsKey(iso3Lang)) {
if ( elements.get(iso3Lang).containsKey(name) ) {
  return elements.get(iso3Lang).get(name);
    } else {
  return lookupElement(name, iso3Lang);
}}
    else {
      loadElements(iso3Lang);
      if  ( elements.containsKey(iso3Lang) && elements.get(iso3Lang).containsKey(name) ) {
        return elements.get(iso3Lang).get(name);
      } else {
        return lookupElement(name, iso3Lang);
      }
    }
  }

  private String lookupElement( String name, String iso3Lang ) {
    String loggerPrefix = getLoggerPrefix("lookupElement", name, iso3Lang);
    ServiceResult<ElementTrl> _result = elementTrlService.getByNameAndIso3(GetByNameAndIso3Query.builder().iso3Language(iso3Lang).name(name).build());
    if ( _result.getIsSuccess() && _result.getData() !=  null ) {
      return _result.getData().getValue();
    } else {
      logger().error(loggerPrefix+"Cannot get element "  + _result.getMessage());
      return name;
    }
  }
  @Override
  public String getAction(String name, String iso3Lang) {
    String loggerPrefix = getLoggerPrefix("getAction", name, iso3Lang);
    if ( elements.containsKey(iso3Lang)) {
      if ( elements.get(iso3Lang).containsKey(name) ) {
        return elements.get(iso3Lang).get(name);
      } else {
        return lookupAction(name, iso3Lang);
      }}
    else {
      loadActions(iso3Lang);
      if  ( elements.containsKey(iso3Lang) && elements.get(iso3Lang).containsKey(name) ) {
        return elements.get(iso3Lang).get(name);
      } else {
        return lookupAction(name, iso3Lang);
      }
    }
  }

  private String lookupAction( String name, String iso3Lang ) {
    String loggerPrefix = getLoggerPrefix("lookupAction", name, iso3Lang);
    ServiceResult<ActionTrl> _result = actionTrlService.getByNameAndIso3(GetByNameAndIso3Query.builder().iso3Language(iso3Lang).name(name).build());
    if ( _result.getIsSuccess() && _result.getData() !=  null ) {
      return _result.getData().getValue();
    } else {
      logger().error(loggerPrefix+"Cannot get element "  + _result.getMessage());
      return name;
    }
  }

  @Override
  public String getMessage(String name, String iso3Lang, String ... params) {
    String loggerPrefix = getLoggerPrefix("getMessage", name, iso3Lang, params);

    String result;

    if ( elements.containsKey(iso3Lang)) {
      if ( elements.get(iso3Lang).containsKey(name) ) {
        result= elements.get(iso3Lang).get(name);
      } else {
        result= lookupMessage(name, iso3Lang);
      }}
    else {
      loadMessages(iso3Lang);
      if  ( elements.containsKey(iso3Lang) && elements.get(iso3Lang).containsKey(name) ) {
        result= elements.get(iso3Lang).get(name);
      } else {
        result= lookupMessage(name, iso3Lang);
      }
    }
    result =  MessageFormat.format(result, params);
    return result;
  }

  private String lookupMessage( String name, String iso3Lang ) {
    String loggerPrefix = getLoggerPrefix("lookupMessage", name, iso3Lang);
    ServiceResult<MessageTrl> _result = messageTrlService.getByNameAndIso3(GetByNameAndIso3Query.builder().iso3Language(iso3Lang).name(name).build());
    if ( _result.getIsSuccess() && _result.getData() !=  null ) {
      return _result.getData().getValue();
    } else {
      logger().error(loggerPrefix+"Cannot get element "  + _result.getMessage());
      return name;
    }
  }
  private void loadElements( String isoLang) {
    elements.remove(isoLang);
  elementTrlService.findByIso3(FindByIso3Query.builder().iso3Language(isoLang).build()).ifSuccess(i18NIsoLangValues -> {
  elements.put(isoLang, i18NIsoLangValues.stream().collect(Collectors.toMap(ElementTrl::getName, ElementTrl::getValue) ));
});
  }
  private void loadActions( String isoLang) {
    actions.remove(isoLang);
    actionTrlService.findByIso3(FindByIso3Query.builder().iso3Language(isoLang).build()).ifSuccess(i18NIsoLangValues -> {
      actions.put(isoLang, i18NIsoLangValues.stream().collect(Collectors.toMap(ActionTrl::getName, ActionTrl::getValue) ));
    });
  }
  private void loadMessages( String isoLang) {
    messsages.remove(isoLang);
    messageTrlService.findByIso3(FindByIso3Query.builder().iso3Language(isoLang).build()).ifSuccess(i18NIsoLangValues -> {
      messsages.put(isoLang, i18NIsoLangValues.stream().collect(Collectors.toMap(MessageTrl::getName, MessageTrl::getValue) ));
    });
  }
}
