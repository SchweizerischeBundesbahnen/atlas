package ch.sbb.importservice.service.bulk.reader;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.ImportFiles;
import java.io.File;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
@IntegrationTest
class BulkImportCsvReaderTest {

  private static final String SERVICE_POINT_UPDATE_HEADER = """
      sloid;number;validFrom;validTo;designationOfficial;designationLong;stopPointType;freightServicePoint;operatingPointType;operatingPointTechnicalTimetableType;meansOfTransport;categories;operatingPointTrafficPointType;sortCodeOfDestinationStation;businessOrganisation;east;north;spatialReference;height
      """;

  @Test
  void shouldReadServicePointUpdateCsvCorrectlyWithNullingAndPipedSet() {
    File file = ImportFiles.getFileByPath("import-files/valid/service-point-update.csv");

    List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> servicePointUpdates =
        BulkImportCsvReader.readLinesFromFileWithNullingValue(
            file, ServicePointUpdateCsvModel.class);

    assertThat(servicePointUpdates).hasSize(1);
    assertThat(servicePointUpdates.getFirst().getAttributesToNull()).containsExactly("height");

    ServicePointUpdateCsvModel expected = ImportFiles.getExpectedServicePointUpdateCsvModel();
    ServicePointUpdateCsvModel actual = servicePointUpdates.getFirst().getObject();
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  void shouldReportAllDataMappingErrors() throws IOException {
    String csvLine = """
        ch:1:sloid:7000;num;tomorrow;31.12.2099;Bern;;STOP;idk;;;CAR|TAXI;;;code;ch:1:sboid:100001;north;1199749.812;LV97;20m
        """;
    BulkImportUpdateContainer<ServicePointUpdateCsvModel> result = BulkImportCsvReader.readObject(
        ServicePointUpdateCsvModel.class, SERVICE_POINT_UPDATE_HEADER, csvLine, 1);

    assertThat(result.getBulkImportLogEntry().getErrors()).hasSize(9);

    List<String> errorMessages = result.getBulkImportLogEntry().getErrors().stream()
        .map(BulkImportError::getErrorMessage)
        .toList();
    assertThat(errorMessages).containsExactlyInAnyOrder(
        "Expected INTEGER but got num in column number",
        "Expected DATE but got tomorrow in column validFrom",
        "Expected ENUM but got STOP in column stopPointType",
        "Expected BOOLEAN but got idk in column freightServicePoint",
        "Expected ENUM but got CAR in column meansOfTransport",
        "Expected ENUM but got TAXI in column meansOfTransport",
        "Expected DOUBLE but got north in column east",
        "Expected ENUM but got LV97 in column spatialReference",
        "Expected DOUBLE but got 20m in column height");
  }
}