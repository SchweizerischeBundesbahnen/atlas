package ch.sbb.atlas.transport.company.entity;

import ch.sbb.atlas.kafka.model.transport.company.SharedTransportCompanyModel;

public interface TransportCompanySharing {

  Long getId();

  String getNumber();

  String getAbbreviation();

  String getDescription();

  String getBusinessRegisterName();

  String getBusinessRegisterNumber();

  void setId(Long id);

  void setNumber(String number);

  void setAbbreviation(String abbreviation);

  void setDescription(String description);

  void setBusinessRegisterName(String businessRegisterName);

  void setBusinessRegisterNumber(String businessRegisterNumber);

  default void setPropertiesFromModel(SharedTransportCompanyModel model) {
    setId(model.getId());
    setNumber(model.getNumber());
    setAbbreviation(model.getAbbreviation());
    setDescription(model.getDescription());
    setBusinessRegisterName(model.getBusinessRegisterName());
    setBusinessRegisterNumber(model.getBusinessRegisterNumber());
  }
}
