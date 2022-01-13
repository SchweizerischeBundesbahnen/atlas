package ch.sbb.timetable.field.number.exceptions;

import ch.sbb.timetable.field.number.api.ErrorResponse;
import ch.sbb.timetable.field.number.api.ErrorResponse.Detail;
import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.entity.Version.Fields;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class ConflictException extends AtlasException {

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

    List<Version> numberOverlappingVersion = overlappingVersions.stream()
                                                                    .filter(i -> Objects.equals(
                                                                        i.getNumber(),
                                                                        newVersion.getNumber()))
                                                                    .collect(Collectors.toList());
    numberOverlappingVersion.forEach(version -> details.add(Detail.builder()
                                                                    .code("TTFN.CONFLICT.NUMBER")
                                                                    .field(Fields.number)
                                                                    .message(
                                                                        "Number {0} already taken from {1} to {2} by {3}")
                                                                    .parameters(
                                                                        List.of(
                                                                            newVersion.getNumber(),
                                                                            version.getValidFrom()
                                                                                   .format(
                                                                                       DateTimeFormatter.ISO_DATE),
                                                                            version.getValidTo()
                                                                                   .format(
                                                                                       DateTimeFormatter.ISO_DATE),
                                                                            version.getTtfnid()))
                                                                    .build()));

    List<Version> swissTimetableFieldNumberOverlappingVersion = overlappingVersions.stream()
                                                                                       .filter(
                                                                                           i -> i.getSwissTimetableFieldNumber()
                                                                                                 .equals(
                                                                                                     newVersion.getSwissTimetableFieldNumber()))
                                                                                       .collect(
                                                                                           Collectors.toList());
    swissTimetableFieldNumberOverlappingVersion.forEach(
        version -> details.add(Detail.builder()
                                     .code("TTFN.CONFLICT.SWISS_NUMBER")
                                     .field(Fields.swissTimetableFieldNumber)
                                     .message(
                                         "SwissTimetableFieldNumber {0} already taken from {1} to {2} by {3}")
                                     .parameters(
                                         List.of(
                                             newVersion.getSwissTimetableFieldNumber(),
                                             version.getValidFrom()
                                                    .format(
                                                        DateTimeFormatter.ISO_DATE),
                                             version.getValidTo()
                                                    .format(
                                                        DateTimeFormatter.ISO_DATE),
                                             version.getTtfnid()))
                                     .build()));
    return details;
  }
}
