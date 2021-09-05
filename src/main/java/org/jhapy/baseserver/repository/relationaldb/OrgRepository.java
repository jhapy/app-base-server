package org.jhapy.baseserver.repository.relationaldb;

import org.jhapy.baseserver.domain.relationaldb.Client;
import org.jhapy.baseserver.domain.relationaldb.Org;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgRepository extends BaseRepository<Org> {}