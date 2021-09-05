package org.jhapy.baseserver.endpoint;

import org.jhapy.baseserver.converter.ClientConverter;
import org.jhapy.baseserver.domain.relationaldb.Client;
import org.jhapy.baseserver.service.ClientService;
import org.jhapy.baseserver.service.CrudRelationalService;
import org.jhapy.dto.domain.ClientDTO;
import org.jhapy.dto.serviceQuery.ServiceResult;
import org.jhapy.dto.serviceQuery.security.GetByExternalIdQuery;
import org.jhapy.dto.serviceQuery.security.GetByExternalIdsQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Alexandre Clavaud.
 * @version 1.0
 * @since 2019-03-07
 */
@RestController
@RequestMapping("/api/clientService")
public class ClientEndpoint extends BaseRelationaldbV2Endpoint<Client, ClientDTO> {

  private final ClientService service;

  public ClientEndpoint(ClientConverter mapper, ClientService service) {
    super(mapper);
    this.service = service;
  }

  @PostMapping(value = "/getByExternalId")
  public ResponseEntity<ServiceResult> getByExternalId(@RequestBody GetByExternalIdQuery query) {
    var loggerPrefix = getLoggerPrefix("getByExternalId");

    return handleResult(
        loggerPrefix,
        mapper.asDTO(service.getByExternalId(query.getExternalId()), getContext(query)));
  }

  @PostMapping(value = "/getByExternalIds")
  public ResponseEntity<ServiceResult> getByExternalIds(@RequestBody GetByExternalIdsQuery query) {
    var loggerPrefix = getLoggerPrefix("getByExternalIds");

    return handleResult(
        loggerPrefix,
        mapper.asDTOList(service.getByExternalIds(query.getExternalIds()), getContext(query)));
  }

  @Override
  protected CrudRelationalService<Client> getService() {
    return service;
  }
}