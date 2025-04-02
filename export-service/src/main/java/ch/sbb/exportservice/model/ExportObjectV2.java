package ch.sbb.exportservice.model;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExportObjectV2 {

  BUSINESS_ORGANISATION(ExportTypes.DEFAULT, "business-organisation"),
  CONTACT_POINT(ExportTypes.DEFAULT, "contact-point"),
  LINE(ExportTypes.DEFAULT, "line"),
  PARKING_LOT(ExportTypes.DEFAULT, "parking-lot"),
  PLATFORM(ExportTypes.DEFAULT, "platform"),
  REFERENCE_POINT(ExportTypes.DEFAULT, "reference-point"),
  RELATION(ExportTypes.DEFAULT, "relation"),
  STOP_POINT(ExportTypes.DEFAULT, "stop-point"),
  SUBLINE(ExportTypes.DEFAULT, "subline"),
  TIMETABLE_FIELD_NUMBER(ExportTypes.DEFAULT, "timetable-field-number"),
  TOILET(ExportTypes.DEFAULT, "toilet"),
  TRANSPORT_COMPANY(List.of(ExportTypeV2.FULL), "transport-company"),
  SERVICE_POINT(ExportTypes.SWISS_WORLD, "service-point"),
  TRAFFIC_POINT(ExportTypes.SWISS_WORLD, "traffic-point"),
  LOADING_POINT(ExportTypes.SWISS_WORLD, "loading-point");

  private final List<ExportTypeV2> supportedExportTypes;
  private final String name;

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

}
