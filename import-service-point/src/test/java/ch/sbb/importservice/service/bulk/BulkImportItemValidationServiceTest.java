package ch.sbb.importservice.service.bulk;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import ch.sbb.atlas.imports.bulk.TrafficPointUpdateCsvModel;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class BulkImportItemValidationServiceTest {

  @Test
  void shouldValidateAndStoreBulkImportLogEntry() {
    BulkImportUpdateContainer<ServicePointUpdateCsvModel> container =
        BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
            .object(ServicePointUpdateCsvModel.builder().build())
            .build();
    BulkImportItemValidationService.validateAll(List.of(container));

    assertThat(container.getBulkImportLogEntry().getErrors()).hasSize(3);
    List<String> errorMessages = container.getBulkImportLogEntry().getErrors().stream().map(BulkImportError::getErrorMessage)
        .toList();
    assertThat(errorMessages).containsExactlyInAnyOrder("SlOID xor number must be given",
        "Field validFrom must not be null", "Field validTo must not be null");
  }

  @Test
  void shouldValidateAndStoreBulkImportLogEntryForTrafficPointUpdateCsvModel() {
    BulkImportUpdateContainer<TrafficPointUpdateCsvModel> container =
        BulkImportUpdateContainer.<TrafficPointUpdateCsvModel>builder()
            .object(TrafficPointUpdateCsvModel.builder().build())
            .build();
    BulkImportItemValidationService.validateAll(List.of(container));

    assertThat(container.getBulkImportLogEntry().getErrors()).hasSize(3);
    List<String> errorMessages = container.getBulkImportLogEntry().getErrors().stream().map(BulkImportError::getErrorMessage)
        .toList();
    assertThat(errorMessages).containsExactlyInAnyOrder("Field sloid must not be null",
        "Field validFrom must not be null", "Field validTo must not be null");
  }

  @Test
  void shouldValidateAndStoreBulkImportLogEntryInvalidServicePointNumber() {
    BulkImportUpdateContainer<ServicePointUpdateCsvModel> container =
        BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
            .object(ServicePointUpdateCsvModel.builder()
                .number(123)
                .validFrom(LocalDate.now())
                .validTo(LocalDate.now().plusDays(1))
                .build())
            .build();
    BulkImportItemValidationService.validateAll(List.of(container));

    assertThat(container.getBulkImportLogEntry().getErrors()).hasSize(1);
    List<String> errorMessages = container.getBulkImportLogEntry().getErrors().stream().map(BulkImportError::getErrorMessage)
        .toList();
    assertThat(errorMessages).containsExactlyInAnyOrder("Invalid Service Point Number");
  }

}