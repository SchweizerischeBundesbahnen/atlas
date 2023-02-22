package ch.sbb.line.directory.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;
import static java.util.stream.Collectors.toList;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.ValidFromDetail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.LineVersion.Fields;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class TemporaryLineValidationException extends AtlasException {

  private static final String CODE_PREFIX = "LIDI.LINE.";
  private static final String MESSAGE_NO_RELATING_VERSIONS = "Temporary version from {0} to {1} exceeds maximum validity of 12 months";
  private static final String MESSAGE_WITH_RELATING_VERSIONS = "Temporary version from {0} to {1} "
      + "is a part of relating temporary versions, which together exceed maximum validity of 12 months";
  private static final String CODE_NO_RELATING_VERSIONS = "TEMPORARY_VERSION_EXCEEDS_MAX_VALIDITY";
  private static final String CODE_WITH_RELATING_VERSIONS = "RELATING_TEMPORARY_VERSIONS_EXCEED_MAX_VALIDITY";

  private static final String ERROR = "Temporary line validation";

  private final List<LineVersion> relatingVersions;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
        .message("Business rule validation failed")
        .error(ERROR)
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    String message = MESSAGE_WITH_RELATING_VERSIONS;
    String code = CODE_WITH_RELATING_VERSIONS;
    if (relatingVersions.size() == 1) {
      message = MESSAGE_NO_RELATING_VERSIONS;
      code = CODE_NO_RELATING_VERSIONS;
    }
    return relatingVersions.stream().map(toErrorDetail(message, code)).collect(toList());
  }

  private Function<LineVersion, Detail> toErrorDetail(String message, String code) {
    return lineVersion -> ValidFromDetail.builder()
        .field(Fields.validTo)
        .message(message)
        .displayInfo(builder()
            .code(CODE_PREFIX + code)
            .with(Fields.validFrom, lineVersion.getValidFrom())
            .with(Fields.validTo, lineVersion.getValidTo())
            .build()).build();
  }

}
