package ch.sbb.exportservice.model;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExportObjectV2 {

  BUSINESS_ORGANISATION(Constants.DEFAULTS),
  CONTACT_POINT(Constants.DEFAULTS),
  LINE(Constants.DEFAULTS),
  PARKING_LOT(Constants.DEFAULTS),
  PLATFORM(Constants.DEFAULTS),
  REFERENCE_POINT(Constants.DEFAULTS),
  RELATION(Constants.DEFAULTS),
  STOP_POINT(Constants.DEFAULTS),
  SUBLINE(Constants.DEFAULTS),
  TIMETABLE_FIELD_NUMBER(Constants.DEFAULTS),
  TOILET(Constants.DEFAULTS),
  TRANSPORT_COMPANY(Constants.DEFAULTS),
  SERVICE_POINT(Constants.SPECIALS),
  TRAFFIC_POINT(Constants.SPECIALS),
  LOADING_POINT(Constants.SPECIALS);

  private final List<ExportTypeV2> supportedExportTypes;

  private static class Constants {

    public static final List<ExportTypeV2> DEFAULTS = List.of(
        ExportTypeV2.FULL,
        ExportTypeV2.ACTUAL,
        ExportTypeV2.FUTURE_TIMETABLE
    );

    public static final List<ExportTypeV2> SPECIALS = List.of(
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
