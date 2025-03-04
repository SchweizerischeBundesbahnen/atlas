package ch.sbb.importservice.service.bulk;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.PlatformReducedUpdateCsvModel;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel;
import ch.sbb.atlas.imports.model.TrafficPointUpdateCsvModel;
import ch.sbb.atlas.imports.model.create.ServicePointCreateCsvModel;
import java.util.List;
import java.util.function.Function;
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
  void shouldValidateAndStoreBulkImportLogEntryForNumberUniqueInSPCreate() {
    BulkImportUpdateContainer<ServicePointCreateCsvModel> container1 =
        BulkImportUpdateContainer.<ServicePointCreateCsvModel>builder()
            .lineNumber(1)
            .object(ServicePointCreateCsvModel.builder()
                .numberShort(100)
                .uicCountryCode(85)
                .build())
            .build();

    BulkImportUpdateContainer<ServicePointCreateCsvModel> container2 =
        BulkImportUpdateContainer.<ServicePointCreateCsvModel>builder()
            .lineNumber(2)
            .object(ServicePointCreateCsvModel.builder()
                .numberShort(100)
                .uicCountryCode(86)
                .build())
            .build();

    BulkImportUpdateContainer<ServicePointCreateCsvModel> container3 =
        BulkImportUpdateContainer.<ServicePointCreateCsvModel>builder()
            .lineNumber(3)
            .object(ServicePointCreateCsvModel.builder()
                .numberShort(100)
                .uicCountryCode(85)
                .build())
            .build();

    BulkImportValidationService.validateUniqueness(List.of(container1, container2, container3));

    assertThat(container1.getBulkImportLogEntry().getErrors()).hasSize(1);
    assertThat(container2.getBulkImportLogEntry()).isNull();
    assertThat(container3.getBulkImportLogEntry().getErrors()).hasSize(1);

    Function<BulkImportUpdateContainer<ServicePointCreateCsvModel>, List<String>> extractErrorMessage =
        container -> container.getBulkImportLogEntry()
            .getErrors().stream().map(BulkImportError::getErrorMessage).toList();

    assertThat(extractErrorMessage.apply(container1)).containsExactlyInAnyOrder(
        "number with value 8500100 occurred more than once");
    assertThat(extractErrorMessage.apply(container3)).containsExactlyInAnyOrder(
        "number with value 8500100 occurred more than once");
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
  void shouldValidateAndStoreBulkImportLogEntryForPlatformReducedUpdateCsvModel() {
    BulkImportUpdateContainer<PlatformReducedUpdateCsvModel> container1 =
        BulkImportUpdateContainer.<PlatformReducedUpdateCsvModel>builder()
            .lineNumber(1)
            .object(PlatformReducedUpdateCsvModel.builder()
                .sloid("sloid:1")
                .build())
            .build();

    BulkImportUpdateContainer<PlatformReducedUpdateCsvModel> container2 =
        BulkImportUpdateContainer.<PlatformReducedUpdateCsvModel>builder()
            .lineNumber(2)
            .object(PlatformReducedUpdateCsvModel.builder()
                .sloid("sloid:2")
                .build())
            .build();

    BulkImportUpdateContainer<PlatformReducedUpdateCsvModel> container3 =
        BulkImportUpdateContainer.<PlatformReducedUpdateCsvModel>builder()
            .lineNumber(3)
            .object(PlatformReducedUpdateCsvModel.builder()
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
