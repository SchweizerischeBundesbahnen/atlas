package ch.sbb.atlas.versionable;

import java.time.LocalDate;

public interface Versionable {

  LocalDate getValidFrom();

  void setValidFrom(LocalDate validFrom);

  LocalDate getValidTo();

  void setValidTo(LocalDate validTo);

  Long getId();

  void setId(Long id);

}
