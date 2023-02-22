package ch.sbb.atlas.base.service.model.entity;

class SboidGenerator extends BusinessIdGenerator {

  public SboidGenerator() {
    super("sboid_seq", "ch:1:sboid:", "sboid");
  }
}