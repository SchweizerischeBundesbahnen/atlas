package ch.sbb.prm.directory.service;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.versioning.model.Versionable;
import java.time.LocalDate;

public interface PrmVersionable extends Versionable {

  String getSloid();

  ServicePointNumber getNumber();

  LocalDate getValidFrom();

  LocalDate getValidTo();

  void setSloid(String sloid);

  void setNumber(ServicePointNumber number);

  Integer getVersion();
}
