package ch.sbb.exportservice.model;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExportObjectV2 {

  BUSINESS_ORGANISATION(ExportTypes.DEFAULT, "business-organisation", "business-organisation-batch"),
  CONTACT_POINT(ExportTypes.DEFAULT, "contact-point", "contact-point-batch"),
  LINE(ExportTypes.DEFAULT, "line", "line-batch"),
  PARKING_LOT(ExportTypes.DEFAULT, "parking-lot", "parking-lot-batch"),
  PLATFORM(ExportTypes.DEFAULT, "platform", "platform-batch"),
  REFERENCE_POINT(ExportTypes.DEFAULT, "reference-point", "reference-point-batch"),
  RELATION(ExportTypes.DEFAULT, "relation", "relation-batch"),
  STOP_POINT(ExportTypes.DEFAULT, "stop-point", "stop-point-batch"),
  SUBLINE(ExportTypes.DEFAULT, "subline", "subline-batch"),
  TIMETABLE_FIELD_NUMBER(ExportTypes.DEFAULT, "timetable-field-number", "ttfn-batch"),
  TOILET(ExportTypes.DEFAULT, "toilet", "toilet-batch"),
  RECORDING_OBLIGATION(List.of(ExportTypeV2.FULL), "recording-obligation", "recording-obligation-batch"),
  TRANSPORT_COMPANY(List.of(ExportTypeV2.FULL), "transport-company", "transport-company-batch"),
  SERVICE_POINT(ExportTypes.SWISS_WORLD, "service-point", "service-point-batch"),
  TRAFFIC_POINT(ExportTypes.SWISS_WORLD, "traffic-point", "traffic-point-batch"),
  LOADING_POINT(ExportTypes.SWISS_WORLD, "loading-point", "loading-point-batch"),
  ;

  private final List<ExportTypeV2> supportedExportTypes;
  private final String name;
  private final String batchServiceName;

  private static class ExportTypes {

    public static final List<ExportTypeV2> DEFAULT = List.of(
        ExportTypeV2.FULL,
        ExportTypeV2.ACTUAL,
        ExportTypeV2.FUTURE_TIMETABLE,
        ExportTypeV2.TIMETABLE_YEARS
    );

    public static final List<ExportTypeV2> SWISS_WORLD = List.of(
        ExportTypeV2.SWISS_FULL,
        ExportTypeV2.SWISS_ACTUAL,
        ExportTypeV2.SWISS_FUTURE_TIMETABLE,
        ExportTypeV2.WORLD_FULL,
        ExportTypeV2.WORLD_ACTUAL,
        ExportTypeV2.WORLD_FUTURE_TIMETABLE
    );

  }

  public boolean isSupportedExportType(ExportTypeV2 exportType) {
    return supportedExportTypes.contains(exportType);
  }

  public static ExportObjectV2 getExportTypeForBatchServiceName(String batchServiceName) {
    return Arrays.stream(ExportObjectV2.values())
        .filter(exportType -> exportType.getBatchServiceName().equals(batchServiceName))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Batch service " + batchServiceName + " not found"));
  }

}
