package org.jhapy.baseserver.client.mailcow;

import org.jhapy.commons.exception.ServiceException;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.mailcow.client.invoker.ApiClient;
import org.jhapy.mailcow.client.model.DefaultMethodResponse;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class MailcowBaseClient implements HasLogger {
  protected final ApiClient apiClient;

  protected MailcowBaseClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  protected Object parseResultAction(DefaultMethodResponse response) throws ServiceException {
    String loggerPrefix = getLoggerPrefix("parseResult");
    if (response.getType().equals(DefaultMethodResponse.TypeEnum.ERROR)) {
      error(
          loggerPrefix,
          "Error : "
              + response.getLog().stream().map(Object::toString).collect(Collectors.joining(",")));
      throw new ServiceException("Error : " + response.getMsg().get(0).toString(), "mailboxClient");
    } else if (response.getType().equals(DefaultMethodResponse.TypeEnum.DANGER)) {
      warn(
          loggerPrefix,
          "Error : "
              + response.getLog().stream()
                  .map(Object::toString)
                  .filter(Objects::nonNull)
                  .collect(Collectors.joining(",")));
      throw new ServiceException(
          "Danger : " + response.getMsg().get(0).toString(), "mailboxClient");
    }
    return response;
  }

  protected Object parseResultAction(List<DefaultMethodResponse> response) throws ServiceException {
    String loggerPrefix = getLoggerPrefix("parseResult");
    if (response == null || (response != null && response.isEmpty())) {
      error(loggerPrefix, "Empty response");
      throw new ServiceException("Empty response", "mailboxClient");
    }
    if (response.size() > 1) {
      error(
          loggerPrefix,
          "More than 1 response..."
              + response.stream()
                  .map(DefaultMethodResponse::toString)
                  .collect(Collectors.joining(",")));
      throw new ServiceException("More than 1 response...", "mailboxClient");
    }
    return parseResultAction(response.get(0));
  }
}