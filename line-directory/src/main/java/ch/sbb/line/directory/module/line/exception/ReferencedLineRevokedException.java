package ch.sbb.line.directory.module.line.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.line.directory.module.line.entity.LineVersionV3.Fields;
import java.util.Set;
import java.util.TreeSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * V3: a member must not be created for (or saved against) a REVOKED referenced line.
 */
@Getter
@RequiredArgsConstructor
public class ReferencedLineRevokedException extends AtlasException {

  private static final String CODE = "LIDI.LINE.CONFLICT.REFERENCED_LINE_REVOKED";
  private static final String MESSAGE = "The referenced line {0} is REVOKED";

  private final String referencedLineSlnid;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.CONFLICT.value())
        .message("A conflict occurred due to a business rule")
        .error("The referenced line " + referencedLineSlnid + " is REVOKED")
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private Set<Detail> getErrorDetails() {
    return Set.of(Detail.builder()
        .field(Fields.referencedLineSlnid)
        .message(MESSAGE)
        .displayInfo(builder()
            .code(CODE)
            .with(Fields.referencedLineSlnid, referencedLineSlnid)
            .build()).build());
  }

}
