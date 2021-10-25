package ch.sbb.timetable.field.number.versioning.model;

import java.time.LocalDate;

public interface Versionable {

  LocalDate getValidFrom();

  LocalDate getValidTo();

  Long getId();

}
