package ch.sbb.atlas.versioning.model;

import ch.sbb.atlas.model.Identifiable;
import java.time.LocalDate;

public interface Versionable extends Identifiable {

  LocalDate getValidFrom();

  void setValidFrom(LocalDate validFrom);

  LocalDate getValidTo();

  void setValidTo(LocalDate validTo);

  void setId(Long id);

}
