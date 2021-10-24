package org.jhapy.baseserver.endpoint;

import org.jhapy.baseserver.converter.OrgConverter;
import org.jhapy.baseserver.domain.relationaldb.Org;
import org.jhapy.baseserver.service.OrgService;
import org.jhapy.baseserver.service.CrudRelationalService;
import org.jhapy.dto.domain.OrgDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Alexandre Clavaud.
 * @version 1.0
 * @since 2019-03-07
 */
@RestController
@RequestMapping("/api/orgService")
public class OrgEndpoint extends BaseRelationaldbV3Endpoint<Org, OrgDTO> {

  private final OrgService service;

  public OrgEndpoint(OrgConverter mapper, OrgService service) {
    super(mapper);
    this.service = service;
  }

  @Override
  protected CrudRelationalService<Org> getService() {
    return service;
  }
}