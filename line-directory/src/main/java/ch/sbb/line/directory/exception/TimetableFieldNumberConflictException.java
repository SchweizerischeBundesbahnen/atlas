package ch.sbb.line.directory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.api.model.ErrorResponse.ValidFromDetail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion.Fields;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class TimetableFieldNumberConflictException extends AtlasException {

  private static final String CODE_PREFIX = "TTFN.CONFLICT.";
  private static final String ERROR = "TimeTableFieldNumber conflict";

  private final TimetableFieldNumberVersion newVersion;
  private final List<TimetableFieldNumberVersion> overlappingVersions;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.CONFLICT.value())
        .error(ERROR)
        .message("A conflict occurred due to a business rule")
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    List<Detail> details = new ArrayList<>();

    overlappingVersions.stream()
        .filter(version -> Objects.equals(version.getNumber(), newVersion.getNumber()))
        .forEach(version -> details.add(toNumberOverlapDetail(version)));

    overlappingVersions.stream()
        .filter(version -> version.getSwissTimetableFieldNumber()
            .equalsIgnoreCase(
                newVersion.getSwissTimetableFieldNumber()))
        .forEach(version -> details.add(
            toSwissTimetableFieldNumberOverlapDetail(version)));

    return details;
  }

  private Detail toSwissTimetableFieldNumberOverlapDetail(TimetableFieldNumberVersion version) {
    return ValidFromDetail.builder()
        .field(Fields.swissTimetableFieldNumber)
        .message(
            "SwissTimetableFieldNumber {0} already taken from {1} to {2} by {3}")
        .displayInfo(DisplayInfo.builder().code(CODE_PREFIX + "SWISS_NUMBER")
            .with(Fields.swissTimetableFieldNumber, newVersion.getSwissTimetableFieldNumber())
            .with(Fields.validFrom, version.getValidFrom())
            .with(Fields.validTo, version.getValidTo())
            .with(Fields.ttfnid, version.getTtfnid())
            .build()).build();
  }

  private Detail toNumberOverlapDetail(TimetableFieldNumberVersion version) {
    return ValidFromDetail.builder()
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
