package ch.sbb.atlas.versioning.model;

import java.time.LocalDate;

public interface Versionable {

  LocalDate getValidFrom();

  void setValidFrom(LocalDate validFrom);

  LocalDate getValidTo();

  void setValidTo(LocalDate validTo);

  Long getId();

  void setId(Long id);

}
