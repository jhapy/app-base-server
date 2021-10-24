package org.jhapy.baseserver.repository.relationaldb;

import org.jhapy.baseserver.domain.relationaldb.Client;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends BaseRepository<Client> {
  @Query("select max(externalId) from Client")
  UUID getMaxExternalId();

  Optional<Client> getByExternalId(UUID externalId);

  List<Client> getByExternalIdIn(List<UUID> externalIds);
}