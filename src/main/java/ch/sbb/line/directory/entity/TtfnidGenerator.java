package ch.sbb.line.directory.entity;

public class TtfnidGenerator extends BusinessIdGenerator {

  public TtfnidGenerator() {
    super("ttfnid_seq", "ch:1:ttfnid:", TimetableFieldNumberVersion.Fields.ttfnid);
  }
}