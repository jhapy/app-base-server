package org.jhapy.baseserver.endpoint;

import org.jhapy.baseserver.converter.TableConverter;
import org.jhapy.baseserver.domain.relationaldb.DbTable;
import org.jhapy.baseserver.service.CrudRelationalService;
import org.jhapy.baseserver.service.DbTableService;
import org.jhapy.dto.domain.DbTableDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Alexandre Clavaud.
 * @version 1.0
 * @since 2019-03-07
 */
@RestController
@RequestMapping("/api/dbTableService")
public class DbTableEndpoint extends BaseRelationaldbV3Endpoint<DbTable, DbTableDTO> {

  private final DbTableService service;

  public DbTableEndpoint(TableConverter mapper, DbTableService service) {
    super(mapper);
    this.service = service;
  }

  @Override
  protected CrudRelationalService<DbTable> getService() {
    return service;
  }
}