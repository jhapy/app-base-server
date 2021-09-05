package org.jhapy.baseserver.client.mailcow;

import org.jhapy.commons.exception.ServiceException;
import org.jhapy.mailcow.client.api.DomainsApi;
import org.jhapy.mailcow.client.invoker.ApiClient;
import org.jhapy.mailcow.client.model.AddDomainBody;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DomainClient extends MailcowBaseClient {
  private DomainsApi domainsApi;

  public DomainClient(ApiClient apiClient) {
    super(apiClient);
    this.domainsApi = new DomainsApi(apiClient);
  }

  public void addDomain(
      String domain,
      String description,
      Long maxMailboxesForDomain,
      Long maxQuotaPerMailbox,
      Long maxQuotaForDomain,
      Long defaultMailboxQuotaForDomain,
      Long countOfAliasesForDomain)
      throws ServiceException {
    AddDomainBody addDomainBody = new AddDomainBody();
    addDomainBody.setDomain(domain);
    addDomainBody.setDescription(description);
    addDomainBody.setMailboxes(BigDecimal.valueOf(maxMailboxesForDomain));
    addDomainBody.setMaxquota(BigDecimal.valueOf(defaultMailboxQuotaForDomain));
    addDomainBody.setQuota(BigDecimal.valueOf(maxQuotaForDomain));
    addDomainBody.setDefquota(BigDecimal.valueOf(maxQuotaPerMailbox));
    addDomainBody.setRestartSogo(BigDecimal.ONE);
    addDomainBody.setAliases(BigDecimal.valueOf(countOfAliasesForDomain));
    addDomainBody.setGal(true);
    addDomainBody.setActive(true);
    super.parseResultAction(domainsApi.createDomain(addDomainBody));
  }
}