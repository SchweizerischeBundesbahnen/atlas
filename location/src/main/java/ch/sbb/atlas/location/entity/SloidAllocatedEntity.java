package ch.sbb.atlas.location.entity;

import lombok.Data;

@Data
public class SloidAllocatedEntity {

  private String sloid;
  private Integer child_seq;
  private Integer grandchild_seq;
}
