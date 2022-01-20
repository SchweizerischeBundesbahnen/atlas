package ch.sbb.timetable.field.number.exceptions;

import ch.sbb.timetable.field.number.api.ErrorResponse;
import ch.sbb.timetable.field.number.api.ErrorResponse.Detail;
import ch.sbb.timetable.field.number.api.ErrorResponse.DisplayInfo;
import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.entity.Version.Fields;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class ConflictException extends AtlasException {

  private static final String CODE_PREFIX = "TTFN.CONFLICT.";

  private final Version newVersion;
  private final List<Version> overlappingVersions;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .httpStatus(HttpStatus.CONFLICT.value())
        .message("A conflict occurred due to a business rule")
        .details(getErrorDetails())
        .build();
  }

  private List<Detail> getErrorDetails() {
    List<Detail> details = new ArrayList<>();

    overlappingVersions.stream()
        .filter(
            version -> Objects.equals(version.getNumber(), newVersion.getNumber()))
        .forEach(version -> details.add(toNumberOverlapDetail(version)));

    overlappingVersions.stream()
        .filter(version -> version.getSwissTimetableFieldNumber()
            .equalsIgnoreCase(newVersion.getSwissTimetableFieldNumber()))
        .forEach(version -> details.add(
            toSwissTimetableFieldNumberOverlapDetail(version)));

    return details;
  }

  private Detail toSwissTimetableFieldNumberOverlapDetail(Version version) {
    return Detail.builder()
        .field(Fields.swissTimetableFieldNumber)
        .message("SwissTimetableFieldNumber {0} already taken from {1} to {2} by {3}")
        .displayInfo(DisplayInfo.builder().code(CODE_PREFIX + "SWISS_NUMBER")
            .with(Fields.swissTimetableFieldNumber,
                newVersion.getSwissTimetableFieldNumber())
            .with(Fields.validFrom, version.getValidFrom())
            .with(Fields.validTo, version.getValidTo())
            .with(Fields.ttfnid, version.getTtfnid())
            .build()).build();
  }

  private Detail toNumberOverlapDetail(Version version) {
    return Detail.builder()
        .field(Fields.number)
        .message("Number {0} already taken from {1} to {2} by {3}")
        .displayInfo(DisplayInfo.builder()
            .code(CODE_PREFIX + "NUMBER")
            .with(Fields.number, newVersion.getNumber())
            .with(Fields.validFrom, version.getValidFrom())
            .with(Fields.validTo, version.getValidTo())
            .with(Fields.ttfnid, version.getTtfnid())
            .build()).build();
  }
}
