package org.jhapy.baseserver.client.mailcow;

import org.jhapy.commons.exception.ServiceException;
import org.jhapy.mailcow.client.api.MailboxesApi;
import org.jhapy.mailcow.client.invoker.ApiClient;
import org.jhapy.mailcow.client.model.AddMailboxBody;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MailboxClient extends MailcowBaseClient {
  private MailboxesApi mailboxesApi;

  public MailboxClient(ApiClient apiClient) {
    super(apiClient);
    this.mailboxesApi = new MailboxesApi(apiClient);
  }

  public void addMailbox(
      String domain, String username, String password, String fullname, Long mailboxQuota)
      throws ServiceException {
    AddMailboxBody addMailboxBody = new AddMailboxBody();
    addMailboxBody.setDomain(domain);
    addMailboxBody.setLocalPart(username);
    addMailboxBody.setPassword(password);
    addMailboxBody.setPassword2(password);
    addMailboxBody.setName(fullname);
    addMailboxBody.setForcePwUpdate(true);
    addMailboxBody.setActive(true);
    addMailboxBody.setQuota(BigDecimal.valueOf(mailboxQuota));

    super.parseResultAction(mailboxesApi.createMailbox(addMailboxBody));
  }
}