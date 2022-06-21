package ch.sbb.business.organisation.directory.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import ch.sbb.business.organisation.directory.service.TransportCompanyCsvModel.TransportCompanyCsvStatus;
import org.junit.jupiter.api.Test;

class TransportCompanyCsvModelTest {

  @Test
  void shouldConvertCsvStatusToStatusSuccessfully() {
    for (TransportCompanyCsvStatus status : TransportCompanyCsvStatus.values()) {
      assertDoesNotThrow(status::toTransportCompanyStatus);
    }
  }
}