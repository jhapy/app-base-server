package org.jhapy.baseserver.domain.relationaldb;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jhapy.baseserver.listener.relationaldb.ClientListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@EntityListeners(ClientListener.class)
@Entity
public class Client extends BaseEntity {
  @Column(nullable = false)
  private String name;

  private String description;

  private String mailboxDomain;
  private String adminMailbox;
  private String adminMailboxPassword;
  private Boolean isMailboxDomainCreated = false;

  @Column(nullable = false, unique = true)
  private Long externalId;

  private Long externalVersion;
}