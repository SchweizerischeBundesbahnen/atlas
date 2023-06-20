package ch.sbb.atlas.business.organisation.entity;

import ch.sbb.atlas.kafka.model.business.organisation.SharedBusinessOrganisationVersionModel;
import ch.sbb.atlas.model.Status;
import java.time.LocalDate;

public interface BusinessOrganisationVersionSharing {

  Long getId();

  String getSboid();

  String getDescriptionDe();

  String getDescriptionFr();

  String getDescriptionIt();

  String getDescriptionEn();

  String getAbbreviationDe();

  String getAbbreviationFr();

  String getAbbreviationIt();

  String getAbbreviationEn();

  Integer getOrganisationNumber();

  Status getStatus();

  LocalDate getValidFrom();

  LocalDate getValidTo();

  void setId(Long id);

  void setSboid(String sboid);

  void setDescriptionDe(String descriptionDe);

  void setDescriptionFr(String descriptionFr);

  void setDescriptionIt(String descriptionIt);

  void setDescriptionEn(String descriptionEn);

  void setAbbreviationDe(String abbreviationDe);

  void setAbbreviationFr(String abbreviationFr);

  void setAbbreviationIt(String abbreviationIt);

  void setAbbreviationEn(String abbreviationEn);

  void setOrganisationNumber(Integer organisationNumber);

  void setStatus(ch.sbb.atlas.model.Status status);

  void setValidFrom(java.time.LocalDate validFrom);

  void setValidTo(java.time.LocalDate validTo);

  default void setPropertiesFromModel(SharedBusinessOrganisationVersionModel model) {
    setId(model.getId());
    setSboid(model.getSboid());
    setAbbreviationDe(model.getAbbreviationDe());
    setAbbreviationFr(model.getAbbreviationFr());
    setAbbreviationIt(model.getAbbreviationIt());
    setAbbreviationEn(model.getAbbreviationEn());
    setDescriptionDe(model.getDescriptionDe());
    setDescriptionFr(model.getDescriptionFr());
    setDescriptionIt(model.getDescriptionIt());
    setDescriptionEn(model.getDescriptionEn());
    setOrganisationNumber(model.getOrganisationNumber());
    setStatus(Status.valueOf(model.getStatus()));
    setValidFrom(model.getValidFrom());
    setValidTo(model.getValidTo());
  }
}
