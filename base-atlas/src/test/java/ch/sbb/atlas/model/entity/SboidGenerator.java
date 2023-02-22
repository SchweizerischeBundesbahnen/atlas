package ch.sbb.atlas.model.entity;

import ch.sbb.atlas.model.entity.BusinessIdGenerator;

class SboidGenerator extends BusinessIdGenerator {

  public SboidGenerator() {
    super("sboid_seq", "ch:1:sboid:", "sboid");
  }
}