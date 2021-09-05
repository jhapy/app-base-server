package org.jhapy.baseserver.client.mailcow;

import org.jhapy.commons.exception.ServiceException;
import org.jhapy.mailcow.client.api.ResourcesApi;
import org.jhapy.mailcow.client.invoker.ApiClient;
import org.jhapy.mailcow.client.model.AddResourceBody;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ResourceClient extends MailcowBaseClient {
  private ResourcesApi resourcesApi;

  public ResourceClient(ApiClient apiClient) {
    super(apiClient);
    this.resourcesApi = new ResourcesApi(apiClient);
  }

  public void addLocationResource(String domain, String name) throws ServiceException {
    AddResourceBody addResourceBody = new AddResourceBody();
    addResourceBody.setDomain(domain);
    addResourceBody.setKind(AddResourceBody.KindEnum.LOCATION);
    addResourceBody.setDescription(name);
    addResourceBody.setMultipleBookings(AddResourceBody.MultipleBookingsEnum._1);
    addResourceBody.setActive(BigDecimal.ONE);

    super.parseResultAction(resourcesApi.createResources(addResourceBody));
  }
}