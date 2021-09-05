package org.jhapy.baseserver.domain.relationaldb;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
public class Org extends BaseEntity {
  @Column(nullable = false)
  private String name;

  private String description;

  @OneToMany(
      mappedBy = "parent",
      cascade = {CascadeType.ALL},
      fetch = FetchType.LAZY)
  private List<Org> childs;

  @ManyToOne private Org parent;
}