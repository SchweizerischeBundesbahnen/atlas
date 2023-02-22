package ch.sbb.line.directory.entity;

import ch.sbb.atlas.model.entity.BusinessIdGenerator;

public class SlnidGenerator extends BusinessIdGenerator {

  public SlnidGenerator() {
    super("slnid_seq", "ch:1:slnid:", "slnid");
  }

}