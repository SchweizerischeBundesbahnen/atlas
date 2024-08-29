package ch.sbb.atlas.servicepointdirectory.repository;

import java.time.LocalDate;

public interface ServicePointSwissWithGeoTransfer {

  String getSloid();

  Long getId();

  LocalDate getValidFrom();

}
