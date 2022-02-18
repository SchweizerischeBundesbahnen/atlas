package ch.sbb.line.directory.entity;

public class SlnidGenerator extends BusinessIdGenerator {

  public SlnidGenerator() {
    super("slnid_seq", "ch:1:slnid:", LineVersion.Fields.slnid);
  }
}