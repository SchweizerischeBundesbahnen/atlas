package ch.sbb.exportservice.model;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExportObjectV2 {

  BUSINESS_ORGANISATION(ExportTypes.DEFAULT),
  CONTACT_POINT(ExportTypes.DEFAULT),
  LINE(ExportTypes.DEFAULT),
  PARKING_LOT(ExportTypes.DEFAULT),
  PLATFORM(ExportTypes.DEFAULT),
  REFERENCE_POINT(ExportTypes.DEFAULT),
  RELATION(ExportTypes.DEFAULT),
  STOP_POINT(ExportTypes.DEFAULT),
  SUBLINE(ExportTypes.DEFAULT),
  TIMETABLE_FIELD_NUMBER(ExportTypes.DEFAULT),
  TOILET(ExportTypes.DEFAULT),
  TRANSPORT_COMPANY(List.of(ExportTypeV2.FULL)),
  SERVICE_POINT(ExportTypes.SWISS_WORLD),
  TRAFFIC_POINT(ExportTypes.SWISS_WORLD),
  LOADING_POINT(ExportTypes.SWISS_WORLD);

  private final List<ExportTypeV2> supportedExportTypes;

  private static class ExportTypes {

    public static final List<ExportTypeV2> DEFAULT = List.of(
        ExportTypeV2.FULL,
        ExportTypeV2.ACTUAL,
        ExportTypeV2.FUTURE_TIMETABLE
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
