package ch.sbb.exportservice.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;

class ExportObjectV2Test {

  @ParameterizedTest
  @EnumSource(value = ExportObjectV2.class, names = {"SERVICE_POINT", "TRAFFIC_POINT", "LOADING_POINT"})
  void shouldSupportSwissAndWorldTimetableYearsVariantsForSwissWorldObjects(ExportObjectV2 exportObject) {
    assertThat(exportObject.isSupportedExportType(ExportTypeV2.SWISS_FULL)).isTrue();
    assertThat(exportObject.isSupportedExportType(ExportTypeV2.SWISS_ACTUAL)).isTrue();
    assertThat(exportObject.isSupportedExportType(ExportTypeV2.SWISS_FUTURE_TIMETABLE)).isTrue();
    assertThat(exportObject.isSupportedExportType(ExportTypeV2.SWISS_TIMETABLE_YEARS)).isTrue();
    assertThat(exportObject.isSupportedExportType(ExportTypeV2.WORLD_FULL)).isTrue();
    assertThat(exportObject.isSupportedExportType(ExportTypeV2.WORLD_ACTUAL)).isTrue();
    assertThat(exportObject.isSupportedExportType(ExportTypeV2.WORLD_FUTURE_TIMETABLE)).isTrue();
    assertThat(exportObject.isSupportedExportType(ExportTypeV2.WORLD_TIMETABLE_YEARS)).isTrue();
  }

  @Test
  void shouldContainExactSwissAndWorld() {
    Set<ExportTypeV2> exportTypes = Set.of(
        ExportTypeV2.SWISS_FULL,
        ExportTypeV2.SWISS_ACTUAL,
        ExportTypeV2.SWISS_FUTURE_TIMETABLE,
        ExportTypeV2.SWISS_TIMETABLE_YEARS,
        ExportTypeV2.WORLD_FULL,
        ExportTypeV2.WORLD_ACTUAL,
        ExportTypeV2.WORLD_FUTURE_TIMETABLE,
        ExportTypeV2.WORLD_TIMETABLE_YEARS
    );

    ExportObjectV2 servicePoint = ExportObjectV2.SERVICE_POINT;
    Set<ExportTypeV2> actualExportTypes = Set.copyOf(servicePoint.getSupportedExportTypes());

    assertThat(actualExportTypes)
        .hasSize(8)
        .isEqualTo(exportTypes);
  }

  @Test
  void shouldNotAllowTimetableYearsForServicePoint() {
    ExportObjectV2 exportObject = ExportObjectV2.SERVICE_POINT;
    assertThat(exportObject.isSupportedExportType(ExportTypeV2.TIMETABLE_YEARS))
        .isFalse();
  }

  @ParameterizedTest
  @EnumSource(value = ExportObjectV2.class, names = {
      "LOADING_POINT",
      "TRAFFIC_POINT",
      "SERVICE_POINT",
      "TRANSPORT_COMPANY",
      "RECORDING_OBLIGATION",
  }, mode = Mode.EXCLUDE)
  void shouldSupportExactlyDefaultTypesForDefaultObjects(ExportObjectV2 exportObject) {
    Set<ExportTypeV2> expectedDefault = Set.of(
        ExportTypeV2.FULL,
        ExportTypeV2.ACTUAL,
        ExportTypeV2.FUTURE_TIMETABLE,
        ExportTypeV2.TIMETABLE_YEARS
    );
    Set<ExportTypeV2> actual = Set.copyOf(exportObject.getSupportedExportTypes());
    assertThat(actual).isEqualTo(expectedDefault);
    assertThat(exportObject.isSupportedExportType(ExportTypeV2.SWISS_FULL)).isFalse();
    assertThat(exportObject.isSupportedExportType(ExportTypeV2.WORLD_FULL)).isFalse();
  }

  @Test
  void shouldSupportOnlyFullForSingleTypeObjects() {
    assertThat(ExportObjectV2.RECORDING_OBLIGATION.getSupportedExportTypes()).containsExactly(ExportTypeV2.FULL);
    assertThat(ExportObjectV2.RECORDING_OBLIGATION.isSupportedExportType(ExportTypeV2.FULL)).isTrue();
    assertThat(ExportObjectV2.RECORDING_OBLIGATION.isSupportedExportType(ExportTypeV2.ACTUAL)).isFalse();

    assertThat(ExportObjectV2.TRANSPORT_COMPANY.getSupportedExportTypes()).containsExactly(ExportTypeV2.FULL);
    assertThat(ExportObjectV2.TRANSPORT_COMPANY.isSupportedExportType(ExportTypeV2.FULL)).isTrue();
    assertThat(ExportObjectV2.TRANSPORT_COMPANY.isSupportedExportType(ExportTypeV2.ACTUAL)).isFalse();
  }

  @Test
  void shouldResolveExportObjectByBatchServiceName() {
    assertThat(ExportObjectV2.getExportTypeForBatchServiceName("business-organisation-batch")).isEqualTo(
        ExportObjectV2.BUSINESS_ORGANISATION);
    assertThat(ExportObjectV2.getExportTypeForBatchServiceName("contact-point-batch")).isEqualTo(ExportObjectV2.CONTACT_POINT);
    assertThat(ExportObjectV2.getExportTypeForBatchServiceName("line-batch")).isEqualTo(ExportObjectV2.LINE);
    assertThat(ExportObjectV2.getExportTypeForBatchServiceName("parking-lot-batch")).isEqualTo(ExportObjectV2.PARKING_LOT);
    assertThat(ExportObjectV2.getExportTypeForBatchServiceName("platform-batch")).isEqualTo(ExportObjectV2.PLATFORM);
    assertThat(ExportObjectV2.getExportTypeForBatchServiceName("reference-point-batch")).isEqualTo(
        ExportObjectV2.REFERENCE_POINT);
    assertThat(ExportObjectV2.getExportTypeForBatchServiceName("relation-batch")).isEqualTo(ExportObjectV2.RELATION);
    assertThat(ExportObjectV2.getExportTypeForBatchServiceName("stop-point-batch")).isEqualTo(ExportObjectV2.STOP_POINT);
    assertThat(ExportObjectV2.getExportTypeForBatchServiceName("subline-batch")).isEqualTo(ExportObjectV2.SUBLINE);
    assertThat(ExportObjectV2.getExportTypeForBatchServiceName("ttfn-batch")).isEqualTo(ExportObjectV2.TIMETABLE_FIELD_NUMBER);
    assertThat(ExportObjectV2.getExportTypeForBatchServiceName("toilet-batch")).isEqualTo(ExportObjectV2.TOILET);
    assertThat(ExportObjectV2.getExportTypeForBatchServiceName("recording-obligation-batch")).isEqualTo(
        ExportObjectV2.RECORDING_OBLIGATION);
    assertThat(ExportObjectV2.getExportTypeForBatchServiceName("transport-company-batch")).isEqualTo(
        ExportObjectV2.TRANSPORT_COMPANY);
    assertThat(ExportObjectV2.getExportTypeForBatchServiceName("service-point-batch")).isEqualTo(ExportObjectV2.SERVICE_POINT);
    assertThat(ExportObjectV2.getExportTypeForBatchServiceName("traffic-point-batch")).isEqualTo(ExportObjectV2.TRAFFIC_POINT);
    assertThat(ExportObjectV2.getExportTypeForBatchServiceName("loading-point-batch")).isEqualTo(ExportObjectV2.LOADING_POINT);
    assertThat(ExportObjectV2.getExportTypeForBatchServiceName("sector-batch")).isEqualTo(ExportObjectV2.SECTOR);
    assertThat(ExportObjectV2.getExportTypeForBatchServiceName("sector-group-batch")).isEqualTo(ExportObjectV2.SECTOR_GROUP);
    assertThat(ExportObjectV2.getExportTypeForBatchServiceName("sectors-and-sectorgroups-batch")).isEqualTo(
        ExportObjectV2.SECTORS_AND_SECTORGROUPS);
  }
}
