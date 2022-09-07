package ch.sbb.line.directory.entity;

import ch.sbb.atlas.base.service.model.entity.BusinessIdGenerator;

public class TtfnidGenerator extends BusinessIdGenerator {

  public TtfnidGenerator() {
    super("ttfnid_seq", "ch:1:ttfnid:", "ttfnid");
  }
}
