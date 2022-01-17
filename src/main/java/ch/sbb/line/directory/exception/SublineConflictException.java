package ch.sbb.line.directory.exception;

import ch.sbb.line.directory.api.ErrorResponse;
import ch.sbb.line.directory.api.ErrorResponse.Detail;
import ch.sbb.line.directory.api.ErrorResponse.DisplayInfo;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.entity.SublineVersion.Fields;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class SublineConflictException extends AtlasException {

  private static final String CODE_PREFIX = "LIDI.SUBLINE.";

  private final SublineVersion newVersion;
  private final List<SublineVersion> overlappingVersions;

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

  private Function<SublineVersion, Detail> toErrorDetail() {
    return lineVersion -> Detail.builder()
                                .field(Fields.swissSublineNumber)
                                .message("SwissSublineNumber {0} already taken from {1} to {2} by {3}")
                                .displayInfo(DisplayInfo.builder()
                                                        .code(CODE_PREFIX + "SWISS_NUMBER")
                                                        .with(Fields.swissSublineNumber,
                                                            newVersion.getSwissSublineNumber())
                                                        .with(Fields.validFrom,
                                                            lineVersion.getValidFrom())
                                                        .with(Fields.validTo,
                                                            lineVersion.getValidTo())
                                                        .with(Fields.slnid, lineVersion.getSlnid())
                                                        .build()).build();
  }

}