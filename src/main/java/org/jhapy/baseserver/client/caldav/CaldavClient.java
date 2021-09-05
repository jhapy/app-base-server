package org.jhapy.baseserver.client.caldav;

import com.github.caldav4j.CalDAVCollection;
import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.util.GenerateQuery;
import com.github.caldav4j.util.ICalendarUtils;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.AltRep;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.RandomUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.jhapy.baseserver.service.ClientService;
import org.jhapy.commons.config.AppProperties;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.dto.caldav.*;
import org.jhapy.dto.utils.LatLng;
import org.springframework.context.annotation.Lazy;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@org.springframework.stereotype.Component
public class CaldavClient implements HasLogger {
  private final ClientService clientService;
  private final AppProperties appProperties;

  public CaldavClient(@Lazy ClientService clientService, AppProperties appProperties) {
    this.clientService = clientService;
    this.appProperties = appProperties;
  }

  protected CloseableHttpClient getClient(Long clientExternalId) {
    String loggerPrefix = getLoggerPrefix("getClient", clientExternalId);
    var client = clientService.getByExternalId(clientExternalId);
    CredentialsProvider credentialsPovider = new BasicCredentialsProvider();
    credentialsPovider.setCredentials(
        new AuthScope(appProperties.getCaldav().getHost(), appProperties.getCaldav().getPort()),
        new UsernamePasswordCredentials(
            client.getAdminMailbox(), client.getAdminMailboxPassword()));
    // new UsernamePasswordCredentials("admin@cfpnc.ma", "@ccess4@admin")
    HttpClientBuilder clientbuilder = HttpClients.custom();
    clientbuilder = clientbuilder.setDefaultCredentialsProvider(credentialsPovider);

    CloseableHttpClient httpClient;
    if (appProperties.getCaldav().getIsSsl()) {
      SSLContextBuilder builder = new SSLContextBuilder();
      try {
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());

        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
        httpClient = clientbuilder.setSSLSocketFactory(sslsf).build();
      } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
        error(loggerPrefix, e, "Unexpected SSL error {0}", e.getMessage());
        httpClient = null;
      }
    } else {
      httpClient = clientbuilder.build();
    }
    return httpClient;
  }

  private String getUrl(String mailbox) {
    var host = appProperties.getCaldav().getHost();
    var port = appProperties.getCaldav().getPort();
    var isSsl = appProperties.getCaldav().getIsSsl();

    String url;
    if (isSsl) url = "https://";
    else url = "http://";
    url += host + ":" + port;
    url += "/SOGo/dav/" + mailbox + "/Calendar/personal/";
    return url;
  }

  public boolean removeCalendarEvent(
      Long clientExternalId, String organiserMailbox, String calendarEntryUid) {
    String loggerPrefix =
        getLoggerPrefix("removeCalendarEvent", organiserMailbox, calendarEntryUid);
    CloseableHttpClient httpClient = getClient(clientExternalId);
    CalDAVCollection calDAVCollection = new CalDAVCollection(getUrl(organiserMailbox));

    try {
      calDAVCollection.delete(
          httpClient, getUrl(organiserMailbox) + "/" + calendarEntryUid + ".ics");
      return true;
    } catch (CalDAV4JException exception) {
      error(loggerPrefix, exception, "Unexpected exception : {0}", exception.getMessage());
      return false;
    }
  }

  public CaldavEventDTO updateCalendarEvent(
      Long clientExternalId,
      String uid,
      String organiserMailbox,
      String relatedObjectName,
      Long relatedObjectId) {
    var loggerPrefix =
        getLoggerPrefix(
            "updateCalendarEvent",
            clientExternalId,
            uid,
            organiserMailbox,
            relatedObjectName,
            relatedObjectId);

    try {
      CloseableHttpClient httpClient = getClient(clientExternalId);

      CalDAVCollection calDAVCollection = new CalDAVCollection(getUrl(organiserMailbox));
      var calendar = calDAVCollection.getCalendarForEventUID(httpClient, uid);
      var event = ICalendarUtils.getMasterEvent(calendar, uid);
      if (StringUtils.isNotBlank(relatedObjectName))
        ICalendarUtils.addOrReplaceProperty(
            event, new XProperty("X-RELATED-OBJECT", relatedObjectName));
      if (relatedObjectId != null)
        ICalendarUtils.addOrReplaceProperty(
            event, new XProperty("X-RELATED-ID", Long.toString(relatedObjectId)));

      TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
      TimeZone timezone = registry.getTimeZone("UTC");
      VTimeZone tz = timezone.getVTimeZone();

      calDAVCollection.updateMasterEvent(httpClient, event, tz);

      return toCaldavEvent(event);
    } catch (CalDAV4JException exception) {
      error(loggerPrefix, exception, "Unexpected exception : {0}", exception.getMessage());
      return null;
    }
  }

  public CaldavEventDTO createCalendarEvent(
      String uid,
      Long clientExternalId,
      String relatedObjectName,
      Long relatedObjectId,
      String mailbox,
      String organiserMailbox,
      String organiserMailboxFullName,
      String summary,
      String description,
      String locationMailbox,
      String locationMailboxFullName,
      LocalDateTime from,
      LocalDateTime to,
      List<String[]> attendees) {
    String loggerPrefix =
        getLoggerPrefix(
            "createCalendarEvent",
            clientExternalId,
            relatedObjectName,
            relatedObjectId,
            mailbox,
            summary,
            from,
            to);
    try {
      CloseableHttpClient httpClient = getClient(clientExternalId);

      CalDAVCollection calDAVCollection = new CalDAVCollection(getUrl(organiserMailbox));

      Uid eventUid;
      if (StringUtils.isBlank(uid)) {
        UidGenerator ug = new RandomUidGenerator();
        eventUid = ug.generateUid();
      } else {
        eventUid = new Uid(uid);
        calDAVCollection.delete(httpClient, Component.VEVENT, uid);
      }

      TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
      TimeZone timezone = registry.getTimeZone("UTC");
      VTimeZone tz = timezone.getVTimeZone();

      Date beginDate =
          ICalendarUtils.createDateTime(
              from.getYear(),
              from.getMonthValue() - 1,
              from.getDayOfMonth(),
              from.getHour(),
              from.getMinute(),
              timezone,
              true);
      Date endDate =
          ICalendarUtils.createDateTime(
              to.getYear(),
              to.getMonthValue() - 1,
              to.getDayOfMonth(),
              to.getHour(),
              to.getMinute(),
              timezone,
              true);
      VEvent nve = new VEvent(beginDate, endDate, summary);
      if (StringUtils.isNotBlank(relatedObjectName))
        ICalendarUtils.addOrReplaceProperty(
            nve, new XProperty("X-RELATED-OBJECT", relatedObjectName));
      if (relatedObjectId != null)
        ICalendarUtils.addOrReplaceProperty(
            nve, new XProperty("X-RELATED-ID", Long.toString(relatedObjectId)));

      nve.getProperties().add(new Description(description));
      var locationProp = new Location(locationMailboxFullName);
      locationProp.getParameters().add(new AltRep(getUrl(locationMailboxFullName)));
      nve.getProperties().add(locationProp);

      Organizer organizer = new Organizer(URI.create("mailto:" + organiserMailbox));
      organizer.getParameters().add(new Cn(organiserMailboxFullName));
      nve.getProperties().add(organizer);

      nve.getProperties().add(eventUid);
      nve.getProperties().add(tz.getTimeZoneId());

      for (String[] s : attendees) {
        Attendee attendee = new Attendee(URI.create("mailto:" + s[1]));
        attendee.getParameters().add(Role.REQ_PARTICIPANT);
        attendee.getParameters().add(PartStat.ACCEPTED);
        attendee.getParameters().add(new Cn(s[0]));
        nve.getProperties().add(attendee);
      }

      debug(loggerPrefix, "Input : {0}", nve.toString());
      var result = calDAVCollection.add(httpClient, nve, tz);
      debug(loggerPrefix, "Output : {0}", result);
      {
        Calendar entry = calDAVCollection.getCalendarForEventUID(httpClient, result);
        ComponentList componentList = entry.getComponents().getComponents(Component.VEVENT);
        Iterator<VEvent> eventIterator = componentList.iterator();
        while (eventIterator.hasNext()) {
          VEvent ve = eventIterator.next();
          var event = toCaldavEvent(ve);
          debug(loggerPrefix, "Found event : {0}", event);
          return event;
        }
        return null;
      }
    } catch (CalDAV4JException | URISyntaxException exception) {
      error(loggerPrefix, exception, "Unexpected exception : {0}", exception.getMessage());
      return null;
    }
  }

  public List<CaldavEventDTO> getCalendarEvents(
      Long clientExternalId, String mailbox, LocalDateTime from, LocalDateTime to) {
    String loggerPrefix = getLoggerPrefix("getCalendarEvents", clientExternalId, mailbox, from, to);
    var events = new ArrayList<CaldavEventDTO>();

    CloseableHttpClient httpClient = getClient(clientExternalId);

    OffsetDateTime fromOffsetDateTime = from.atOffset(ZoneOffset.UTC);
    OffsetDateTime toOffsetDateTime = to.atOffset(ZoneOffset.UTC);
    DateTime fromDateTime =
        ICalendarUtils.createDateTime(
            fromOffsetDateTime.getYear(),
            fromOffsetDateTime.getMonthValue() - 1,
            fromOffsetDateTime.getDayOfMonth(),
            fromOffsetDateTime.getHour(),
            fromOffsetDateTime.getMinute(),
            null,
            true);
    DateTime toDateTime =
        ICalendarUtils.createDateTime(
            toOffsetDateTime.getYear(),
            toOffsetDateTime.getMonthValue() - 1,
            toOffsetDateTime.getDayOfMonth(),
            toOffsetDateTime.getHour(),
            toOffsetDateTime.getMinute(),
            null,
            true);
    try {
      GenerateQuery gq = new GenerateQuery();
      gq.setFilter("VEVENT");
      gq.setTimeRange(fromDateTime, toDateTime);

      debug(loggerPrefix, "Input : {0}", gq.prettyPrint());

      CalDAVCollection calDAVCollection = new CalDAVCollection(getUrl(mailbox));
      List<Calendar> result = calDAVCollection.queryCalendars(httpClient, gq.generate());
      Iterator<Calendar> iterator = result.iterator();
      while (iterator.hasNext()) {
        Calendar entry = iterator.next();
        if (entry.getComponents() != null) {
          ComponentList componentList =
              entry.getComponents().getComponents(net.fortuna.ical4j.model.Component.VEVENT);
          Iterator<VEvent> eventIterator = componentList.iterator();
          while (eventIterator.hasNext()) {
            VEvent ve = eventIterator.next();
            debug(loggerPrefix, "Found event, initial : {0}", ve);
            var event = toCaldavEvent(ve);
            debug(loggerPrefix, "Found event, converted : {0}", event);
            events.add(event);
          }
        }
      }
    } catch (CalDAV4JException calDAV4JException) {
      error(
          loggerPrefix,
          calDAV4JException,
          "Unexpected exception : {0}",
          calDAV4JException.getMessage());
      return null;
    }
    return events;
  }

  private CaldavEventDTO toCaldavEvent(VEvent vEvent) {
    if (vEvent == null) return null;

    var caldavEvent = new CaldavEventDTO();
    caldavEvent.setSummary(vEvent.getSummary() == null ? null : vEvent.getSummary().getValue());
    caldavEvent.setStatus(
        vEvent.getStatus() == null
            ? null
            : CaldavEventStatusEnum.valueOf(vEvent.getStatus().getValue()));
    caldavEvent.setClassification(
        vEvent.getClassification() == null ? null : vEvent.getClassification().getValue());
    caldavEvent.setDescription(
        vEvent.getDescription() == null ? null : vEvent.getDescription().getValue());
    caldavEvent.setLocation(vEvent.getLocation() == null ? null : vEvent.getLocation().getValue());
    caldavEvent.setOrganizer(
        vEvent.getOrganizer() == null ? null : vEvent.getOrganizer().getCalAddress());
    caldavEvent.setPriority(
        vEvent.getPriority() == null
            ? null
            : CaldavEventPriorityEnum.valueOf(vEvent.getPriority().getValue()));
    caldavEvent.setGeographicPosition(
        vEvent.getGeographicPos() == null
            ? null
            : LatLng.latlng(
                vEvent.getGeographicPos().getLatitude().longValue(),
                vEvent.getGeographicPos().getLongitude().longValue()));
    caldavEvent.setTransparency(
        vEvent.getTransparency() == null
            ? null
            : CaldavEventTransparencyEnum.valueOf(vEvent.getTransparency().getValue()));
    caldavEvent.setSequenceNo(
        vEvent.getSequence() == null ? null : vEvent.getSequence().getSequenceNo());
    caldavEvent.setUrl(vEvent.getUrl() == null ? null : vEvent.getUrl().getUri());
    caldavEvent.setUid(vEvent.getUid() == null ? null : vEvent.getUid().getValue());

    caldavEvent.setCreated(
        vEvent.getCreated() == null ? null : vEvent.getCreated().getDateTime().toInstant());
    caldavEvent.setUpdated(
        vEvent.getLastModified() == null
            ? null
            : vEvent.getLastModified().getDateTime().toInstant());
    // caldavEvent.setDateStamp(vEvent.getDateStamp() == null ? null :
    // LocalDateTime.ofInstant(vEvent.getDateStamp().getDateTime().toInstant(),
    // vEvent.getDateStamp().getTimeZone().toZoneId()));

    caldavEvent.setStartDate(
        vEvent.getStartDate() == null
            ? null
            : LocalDateTime.ofInstant(
                vEvent.getStartDate().getDate().toInstant(),
                vEvent.getStartDate().getTimeZone() == null
                    ? ZoneId.of("UTC")
                    : vEvent.getStartDate().getTimeZone().toZoneId()));
    caldavEvent.setEndDate(
        vEvent.getEndDate() == null
            ? null
            : LocalDateTime.ofInstant(
                vEvent.getEndDate().getDate().toInstant(),
                vEvent.getEndDate().getTimeZone() == null
                    ? ZoneId.of("UTC")
                    : vEvent.getEndDate().getTimeZone().toZoneId()));
    caldavEvent.setDuration(
        vEvent.getDuration() == null ? null : toCaldavDuration(vEvent.getDuration().getDuration()));
    caldavEvent.setRelatedObjectName(
        vEvent.getProperties().getProperty("X-RELATED-OBJECT") == null
            ? null
            : ((XProperty) vEvent.getProperties().getProperty("X-RELATED-OBJECT")).getValue());
    if (vEvent.getProperties().getProperty("X-RELATED-ID") != null)
      caldavEvent.setRelatedObjectId(
          Long.valueOf(
              ((XProperty) vEvent.getProperties().getProperty("X-RELATED-ID")).getValue()));

    return caldavEvent;
  }

  private CaldavDurationDTO toCaldavDuration(Dur duration) {
    if (duration == null) return null;
    var caldavDuration = new CaldavDurationDTO();
    caldavDuration.setDays(duration.getDays());
    caldavDuration.setHours(duration.getHours());
    caldavDuration.setMinutes(duration.getMinutes());
    caldavDuration.setNegative(duration.isNegative());
    caldavDuration.setSeconds(duration.getSeconds());
    caldavDuration.setWeeks(duration.getWeeks());
    return caldavDuration;
  }

  private double calcDuration(VEvent ve) {
    return (ve.getEndDate().getDate().getTime() - ve.getStartDate().getDate().getTime())
        / (1000. * 60. * 60.);
  }
}