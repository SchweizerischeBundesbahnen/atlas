package ch.sbb.line.directory.exception;

import static ch.sbb.line.directory.api.ErrorResponse.DisplayInfo.builder;

import ch.sbb.line.directory.api.ErrorResponse;
import ch.sbb.line.directory.api.ErrorResponse.Detail;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.LineVersion.Fields;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class LineConflictException extends AtlasException {

  private static final String CODE_PREFIX = "LIDI.LINE.CONFLICT.";

  private final LineVersion newVersion;
  private final List<LineVersion> overlappingVersions;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
                        .httpStatus(HttpStatus.CONFLICT.value())
                        .message("A conflict occurred due to a business rule")
                        .details(getErrorDetails())
                        .build();
  }

  private List<Detail> getErrorDetails() {
    return overlappingVersions.stream().map(toErrorDetail()).collect(Collectors.toList());
  }

  private Function<LineVersion, Detail> toErrorDetail() {
    return lineVersion -> Detail.builder()
                                .field(Fields.swissLineNumber)
                                .message("SwissLineNumber {0} already taken from {1} to {2} by {3}")
                                .displayInfo(builder()
                                    .code(CODE_PREFIX + "SWISS_NUMBER")
                                    .with(Fields.swissLineNumber, newVersion.getSwissLineNumber())
                                    .with(Fields.validFrom, lineVersion.getValidFrom())
                                    .with(Fields.validTo, lineVersion.getValidTo())
                                    .with(Fields.slnid, lineVersion.getSlnid())
                                    .build()).build();
  }

}
