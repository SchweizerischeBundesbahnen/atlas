package ch.sbb.importservice.service.bulk;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import ch.sbb.atlas.imports.bulk.TrafficPointUpdateCsvModel;
import java.util.List;
import org.junit.jupiter.api.Test;

class BulkImportValidationServiceTest {

  @Test
  void shouldValidateAndStoreBulkImportLogEntry() {
    BulkImportUpdateContainer<ServicePointUpdateCsvModel> container1 =
        BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
            .lineNumber(1)
            .object(ServicePointUpdateCsvModel.builder()
                .sloid("sloid:1")
                .build())
            .build();

    BulkImportUpdateContainer<ServicePointUpdateCsvModel> container2 =
        BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
            .lineNumber(2)
            .object(ServicePointUpdateCsvModel.builder()
                .sloid("sloid:2")
                .build())
            .build();

    BulkImportUpdateContainer<ServicePointUpdateCsvModel> container3 =
        BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
            .lineNumber(3)
            .object(ServicePointUpdateCsvModel.builder()
                .sloid("sloid:1")
                .build())
            .build();

    BulkImportValidationService.validateUniqueness(List.of(container1, container2, container3));

    assertThat(container1.getBulkImportLogEntry().getErrors()).hasSize(1);
    assertThat(container2.getBulkImportLogEntry()).isNull();
    assertThat(container3.getBulkImportLogEntry().getErrors()).hasSize(1);
    List<String> errorMessages = container1.getBulkImportLogEntry().getErrors().stream().map(BulkImportError::getErrorMessage)
        .toList();
    assertThat(errorMessages).containsExactlyInAnyOrder("sloid with value sloid:1 occurred more than once");
  }

  @Test
  void shouldValidateAndStoreBulkImportLogEntryForTrafficPointUpdateCsvModel() {
    BulkImportUpdateContainer<TrafficPointUpdateCsvModel> container1 =
        BulkImportUpdateContainer.<TrafficPointUpdateCsvModel>builder()
            .lineNumber(1)
            .object(TrafficPointUpdateCsvModel.builder()
                .sloid("sloid:1")
                .build())
            .build();

    BulkImportUpdateContainer<TrafficPointUpdateCsvModel> container2 =
        BulkImportUpdateContainer.<TrafficPointUpdateCsvModel>builder()
            .lineNumber(2)
            .object(TrafficPointUpdateCsvModel.builder()
                .sloid("sloid:2")
                .build())
            .build();

    BulkImportUpdateContainer<TrafficPointUpdateCsvModel> container3 =
        BulkImportUpdateContainer.<TrafficPointUpdateCsvModel>builder()
            .lineNumber(3)
            .object(TrafficPointUpdateCsvModel.builder()
                .sloid("sloid:1")
                .build())
            .build();

    BulkImportValidationService.validateUniqueness(List.of(container1, container2, container3));

    assertThat(container1.getBulkImportLogEntry().getErrors()).hasSize(1);
    assertThat(container2.getBulkImportLogEntry()).isNull();
    assertThat(container3.getBulkImportLogEntry().getErrors()).hasSize(1);
    List<String> errorMessages = container1.getBulkImportLogEntry().getErrors().stream().map(BulkImportError::getErrorMessage)
        .toList();
    assertThat(errorMessages).containsExactlyInAnyOrder("sloid with value sloid:1 occurred more than once");
  }

  @Test
  void shouldValidateAndStoreBulkImportLogEntryIgnoringNulls() {
    BulkImportUpdateContainer<ServicePointUpdateCsvModel> container1 =
        BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
            .lineNumber(1)
            .object(ServicePointUpdateCsvModel.builder()
                .build())
            .build();

    BulkImportUpdateContainer<ServicePointUpdateCsvModel> container2 =
        BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
            .lineNumber(2)
            .object(ServicePointUpdateCsvModel.builder()
                .build())
            .build();

    BulkImportUpdateContainer<ServicePointUpdateCsvModel> container3 =
        BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
            .lineNumber(3)
            .build();

    BulkImportValidationService.validateUniqueness(List.of(container1, container2, container3));

    assertThat(container1.getBulkImportLogEntry()).isNull();
    assertThat(container2.getBulkImportLogEntry()).isNull();
    assertThat(container3.getBulkImportLogEntry()).isNull();
  }

}