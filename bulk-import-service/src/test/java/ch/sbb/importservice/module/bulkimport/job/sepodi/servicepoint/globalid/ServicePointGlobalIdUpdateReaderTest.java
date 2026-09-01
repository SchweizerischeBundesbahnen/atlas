package ch.sbb.importservice.module.bulkimport.job.sepodi.servicepoint.globalid;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportStatus;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.model.BusinessObjectType;
import ch.sbb.atlas.imports.bulk.model.ImportType;
import ch.sbb.atlas.imports.model.ServicePointGlobalIdUpdateCsvModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.module.bulkimport.service.ImportFiles;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;

@IntegrationTest
class ServicePointGlobalIdUpdateReaderTest {

  private final ServicePointGlobalIdUpdateReader reader = new ServicePointGlobalIdUpdateReader();

  @Test
  void shouldBeRegisteredForTheSepodiGlobalIdUpdateScenario() {
    assertThat(reader.getBulkImportConfig().getApplication()).isEqualTo(ApplicationType.SEPODI);
    assertThat(reader.getBulkImportConfig().getObjectType()).isEqualTo(BusinessObjectType.SERVICE_POINT_GLOBAL_ID);
    assertThat(reader.getBulkImportConfig().getImportType()).isEqualTo(ImportType.UPDATE);
  }

  @Test
  void shouldExposeTheGlobalIdCsvModelSoTheTemplateAndParserMatch() {
    assertThat(reader.getCsvModelClass()).isEqualTo(ServicePointGlobalIdUpdateCsvModel.class);
  }

  @Test
  void shouldReadEveryLineOfTheGlobalIdUpdateFile() {
    // Given
    File file = ImportFiles.getFileByPath("import-files/valid/service-point-global-id-update.csv");

    // When
    List<BulkImportUpdateContainer<?>> containers = reader.apply(file);

    // Then
    assertThat(containers).hasSize(3);
    assertThat(containers).extracting(BulkImportUpdateContainer::getLineNumber).containsExactly(2, 3, 4);
  }

  @Test
  void shouldReadSloidNumberAndGlobalIdOfAFullyPopulatedLine() {
    // Given
    File file = ImportFiles.getFileByPath("import-files/valid/service-point-global-id-update.csv");

    // When
    ServicePointGlobalIdUpdateCsvModel firstLine = (ServicePointGlobalIdUpdateCsvModel) reader.apply(file).getFirst()
        .getObject();

    // Then
    assertThat(firstLine.getSloid()).isEqualTo("ch:1:sloid:5770");
    assertThat(firstLine.getNumber()).isEqualTo(1105770);
    assertThat(firstLine.getGlobalId()).isEqualTo("de:05770:1282");
  }

  @Test
  void shouldReadALineIdentifiedByNumberAlone() {
    // Given
    File file = ImportFiles.getFileByPath("import-files/valid/service-point-global-id-update.csv");

    // When
    ServicePointGlobalIdUpdateCsvModel numberOnly = (ServicePointGlobalIdUpdateCsvModel) reader.apply(file).get(1).getObject();

    // Then
    assertThat(numberOnly.getSloid()).isNull();
    assertThat(numberOnly.getNumber()).isEqualTo(1105771);
    assertThat(numberOnly.getGlobalId()).isEqualTo("de:05770:1283");
  }

  @Test
  void shouldReportTheNullingValueAsAttributeToNullSoRemovalStaysExplicit() {
    // Given
    File file = ImportFiles.getFileByPath("import-files/valid/service-point-global-id-update.csv");

    // When
    BulkImportUpdateContainer<?> nulledLine = reader.apply(file).getLast();

    // Then
    assertThat(nulledLine.getAttributesToNull()).containsExactly(ServicePointGlobalIdUpdateCsvModel.Fields.globalId);
    assertThat(((ServicePointGlobalIdUpdateCsvModel) nulledLine.getObject()).getSloid()).isEqualTo("ch:1:sloid:5772");
  }

  @Test
  void shouldNotFlagAValidFileWithDataValidationErrors() {
    // Given
    File file = ImportFiles.getFileByPath("import-files/valid/service-point-global-id-update.csv");

    // When
    List<BulkImportUpdateContainer<?>> containers = reader.apply(file);

    // Then
    assertThat(containers).noneMatch(BulkImportUpdateContainer::hasDataValidationErrors);
  }

  @Test
  void shouldRejectALineThatIdentifiesNoServicePointAtAll() {
    // Given
    File file = ImportFiles.getFileByPath("import-files/invalid/service-point-global-id-update-invalid.csv");

    // When
    BulkImportUpdateContainer<?> withoutIdentifier = reader.apply(file).getFirst();

    // Then
    assertThat(withoutIdentifier.hasDataValidationErrors()).isTrue();
    assertThat(withoutIdentifier.getBulkImportLogEntry().getStatus()).isEqualTo(BulkImportStatus.DATA_VALIDATION_ERROR);
    assertThat(withoutIdentifier.getBulkImportLogEntry().getErrors()).extracting("errorMessage")
        .contains("SlOID or number must be given");
  }

  @Test
  void shouldRejectALineWithAnInvalidServicePointNumber() {
    // Given
    File file = ImportFiles.getFileByPath("import-files/invalid/service-point-global-id-update-invalid.csv");

    // When
    BulkImportUpdateContainer<?> invalidNumber = reader.apply(file).get(1);

    // Then
    assertThat(invalidNumber.hasDataValidationErrors()).isTrue();
    assertThat(invalidNumber.getBulkImportLogEntry().getErrors()).extracting("errorMessage")
        .contains("Invalid Service Point Number");
  }

  @Test
  void shouldRejectAFileThatUsesTheSameGlobalIdTwice() {
    // Given
    File file = ImportFiles.getFileByPath("import-files/invalid/service-point-global-id-update-duplicates.csv");

    // When
    List<BulkImportUpdateContainer<?>> containers = reader.apply(file);

    // Then
    assertThat(containers).allMatch(BulkImportUpdateContainer::hasDataValidationErrors);
    assertThat(containers.getFirst().getBulkImportLogEntry().getErrors()).extracting("errorMessage")
        .contains("globalId with value de:05770:1282 occurred more than once");
  }
}
