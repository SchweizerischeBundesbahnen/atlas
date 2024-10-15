package ch.sbb.importservice.service.bulk.reader;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.PlatformUpdateCsvModel;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import ch.sbb.atlas.imports.bulk.TrafficPointUpdateCsvModel;
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

  private static final String TRAFFIC_POINT_UPDATE_HEADER = """
      sloid;validFrom;validTo;designation;designationOperational;length;boardingAreaHeight;compassDirection;east;north;spatialReference;height;parentSloid
      """;

  private static final String PLATFORM_UPDATE_HEADER = """
      sloid;validFrom;validTo;additionalInformation;height;inclinationLongitudinal;infoOpportunities;partialElevation;tactileSystem;vehicleAccess;wheelchairAreaLength;wheelchairAreaWidth
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
  void shouldReadTrafficPointUpdateCsvCorrectlyWithNullingAndPipedSet() {
    File file = ImportFiles.getFileByPath("import-files/valid/traffic-point-update.csv");

    List<BulkImportUpdateContainer<TrafficPointUpdateCsvModel>> trafficPointUpdates =
        BulkImportCsvReader.readLinesFromFileWithNullingValue(
            file, TrafficPointUpdateCsvModel.class);

    assertThat(trafficPointUpdates).hasSize(1);
    assertThat(trafficPointUpdates.getFirst().getAttributesToNull()).containsExactly("parentSloid");

    TrafficPointUpdateCsvModel expected = ImportFiles.getExpectedTrafficPointUpdateCsvModel();
    TrafficPointUpdateCsvModel actual = trafficPointUpdates.getFirst().getObject();
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  void shouldReadPlatformUpdateCsvCorrectlyWithNullingAndPipedSet() {
    File file = ImportFiles.getFileByPath("import-files/valid/platform-update.csv");

    List<BulkImportUpdateContainer<PlatformUpdateCsvModel>> platformUpdates =
        BulkImportCsvReader.readLinesFromFileWithNullingValue(
            file, PlatformUpdateCsvModel.class);

    assertThat(platformUpdates).hasSize(1);
    assertThat(platformUpdates.getFirst().getAttributesToNull()).containsExactly("wheelchairAreaWidth");

    PlatformUpdateCsvModel expected = ImportFiles.getExpectedPlatformUpdateCsvModel();
    PlatformUpdateCsvModel actual = platformUpdates.getFirst().getObject();
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  void shouldReportAllDataMappingErrorsForServicePointUpdate() throws IOException {
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

  @Test
  void shouldReportAllDataMappingErrorsForTrafficPointUpdate() throws IOException {
    String csvLine = """
        ch:1:sloid:7000:5;num;tomorrow;Bern;Bern;STOP;idk;aaa;east;north;111;height;ch:1:sloid:7000
        """;
    BulkImportUpdateContainer<TrafficPointUpdateCsvModel> result = BulkImportCsvReader.readObject(
        TrafficPointUpdateCsvModel.class, TRAFFIC_POINT_UPDATE_HEADER, csvLine, 1);

    assertThat(result.getBulkImportLogEntry().getErrors()).hasSize(9);

    List<String> errorMessages = result.getBulkImportLogEntry().getErrors().stream()
        .map(BulkImportError::getErrorMessage)
        .toList();
    assertThat(errorMessages).containsExactlyInAnyOrder(
        "Expected DATE but got num in column validFrom",
        "Expected DATE but got tomorrow in column validTo",
        "Expected DOUBLE but got STOP in column length",
        "Expected DOUBLE but got idk in column boardingAreaHeight",
        "Expected DOUBLE but got aaa in column compassDirection",
        "Expected DOUBLE but got east in column east",
        "Expected DOUBLE but got north in column north",
        "Expected ENUM but got 111 in column spatialReference",
        "Expected DOUBLE but got height in column height");
  }

  @Test
  void shouldReportAllDataMappingErrorsForPlatformUpdate() throws IOException {
    String csvLine = """
        ch:1:sloid:88253:0:1;num;tomorrow;5.000;Bern;STOP;idk;aaa;east;north;fake;height
        """;
    BulkImportUpdateContainer<PlatformUpdateCsvModel> result = BulkImportCsvReader.readObject(
        PlatformUpdateCsvModel.class, PLATFORM_UPDATE_HEADER, csvLine, 1);

    assertThat(result.getBulkImportLogEntry().getErrors()).hasSize(10);

    List<String> errorMessages = result.getBulkImportLogEntry().getErrors().stream()
        .map(BulkImportError::getErrorMessage)
        .toList();
    assertThat(errorMessages).containsExactlyInAnyOrder(
        "Expected DATE but got num in column validFrom",
        "Expected DATE but got tomorrow in column validTo",
        "Expected DOUBLE but got Bern in column height",
        "Expected DOUBLE but got STOP in column inclinationLongitudinal",
        "Expected ENUM but got idk in column infoOpportunities",
        "Expected BOOLEAN but got aaa in column partialElevation",
        "Expected ENUM but got east in column tactileSystem",
        "Expected ENUM but got north in column vehicleAccess",
        "Expected DOUBLE but got fake in column wheelchairAreaLength",
        "Expected DOUBLE but got height in column wheelchairAreaWidth"
    );
  }

}