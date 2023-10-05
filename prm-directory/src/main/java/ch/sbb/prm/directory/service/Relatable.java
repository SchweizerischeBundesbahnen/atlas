package ch.sbb.prm.directory.service;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import java.time.LocalDate;

public interface Relatable {

  String getSloid();

  ServicePointNumber getNumber();

  LocalDate getValidFrom();

  LocalDate getValidTo();

  String getParentServicePointSloid();

}
