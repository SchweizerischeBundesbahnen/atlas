package ch.sbb.business.organisation.directory.entity;

import ch.sbb.atlas.base.service.model.entity.BusinessIdGenerator;

public class SboidGenerator extends BusinessIdGenerator {

  public SboidGenerator() {
    super("sboid_seq", "ch:1:sboid:", "sboid");
  }
}