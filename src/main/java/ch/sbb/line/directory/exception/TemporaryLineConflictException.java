package ch.sbb.line.directory.exception;

import ch.sbb.line.directory.api.ErrorResponse;
import ch.sbb.line.directory.api.ErrorResponse.Detail;
import ch.sbb.line.directory.api.ErrorResponse.DisplayInfo;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.LineVersion.Fields;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class TemporaryLineConflictException extends AtlasException {

  private static final String CODE_PREFIX = "LIDI.LINE.";
  private static final String MESSAGE_NO_RELATING_VERSIONS = "Temporary version from {0} to {1} exceeds maximum validity of 12 months";
  private static final String MESSAGE_WITH_RELATING_VERSIONS = "Temporary version from {0} to {1} "
      + "is a part of relating temporary versions, which together exceed maximum validity of 12 months";

  private final List<LineVersion> relatingVersions;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .httpStatus(HttpStatus.CONFLICT.value())
        .message("A conflict occurred due to a business rule")
        .details(getErrorDetails())
        .build();
  }

  private List<Detail> getErrorDetails() {
    String message = relatingVersions.size() == 1 ? MESSAGE_NO_RELATING_VERSIONS : MESSAGE_WITH_RELATING_VERSIONS;
    return relatingVersions.stream().map(toErrorDetail(message)).collect(Collectors.toList());
  }

  private Function<LineVersion, Detail> toErrorDetail(String message) {
    return lineVersion -> Detail.builder()
        .field(Fields.validTo)
        .message(message)
        .displayInfo(DisplayInfo.builder()
            .code(CODE_PREFIX + "TEMPORARY_VERSION_EXCEEDS_MAX_VALIDITY")
            .with(Fields.validFrom,
                lineVersion.getValidFrom())
            .with(Fields.validTo,
                lineVersion.getValidTo())
            .build()).build();
  }

}
