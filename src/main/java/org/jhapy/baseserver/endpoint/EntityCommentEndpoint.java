package org.jhapy.baseserver.endpoint;

import org.jhapy.baseserver.converter.EntityCommentConverter;
import org.jhapy.baseserver.domain.relationaldb.EntityComment;
import org.jhapy.baseserver.service.CrudRelationalService;
import org.jhapy.baseserver.service.EntityCommentService;
import org.jhapy.dto.domain.EntityCommentDTO;
import org.jhapy.dto.serviceQuery.ServiceResult;
import org.jhapy.dto.serviceQuery.generic.GetEntityCommentsQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Alexandre Clavaud.
 * @version 1.0
 * @since 2019-03-07
 */
@RestController
@RequestMapping("/api/entityCommentService")
public class EntityCommentEndpoint
    extends BaseRelationaldbV3Endpoint<EntityComment, EntityCommentDTO> {
  private final EntityCommentService service;

  public EntityCommentEndpoint(EntityCommentConverter mapper, EntityCommentService service) {
    super(mapper);
    this.service = service;
  }

  @PostMapping(value = "/getEntityComments")
  public ResponseEntity<ServiceResult> getEntityComments(
      @RequestBody GetEntityCommentsQuery query) {
    var loggerPrefix = getLoggerPrefix("getEntityComments");

    List<EntityComment> result =
        service.getEntityComments(
            query.getRelatedEntityId(), query.getRelatedEntityName(), query.getSince());
    return handleResult(loggerPrefix, mapper.asDTOList(result, getContext(query)));
  }

  @Override
  protected CrudRelationalService<EntityComment> getService() {
    return service;
  }
}