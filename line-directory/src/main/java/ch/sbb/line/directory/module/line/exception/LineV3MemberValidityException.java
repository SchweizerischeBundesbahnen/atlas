package ch.sbb.line.directory.module.line.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.line.directory.module.line.entity.LineVersionV3.Fields;
import java.util.Set;
import java.util.TreeSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * V3: the saved member lies (partly) outside the overall validity of its referenced line.
 */
@Getter
@RequiredArgsConstructor
public class LineV3MemberValidityException extends AtlasException {

  private static final String CODE = "LIDI.LINE.CONFLICT.SUBLINE_VALIDITY";
  private static final String MESSAGE = "The member validity lies outside of the referenced line validity from {0} to {1}";

  private final transient DateRange referencedLineValidity;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.CONFLICT.value())
        .message("A conflict occurred due to a business rule")
        .error("The member validity lies outside of the referenced line validity from "
            + referencedLineValidity.getFrom() + " to " + referencedLineValidity.getTo())
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private Set<Detail> getErrorDetails() {
    return Set.of(Detail.builder()
        .field(Fields.validFrom)
        .message(MESSAGE)
        .displayInfo(builder()
            .code(CODE)
            .with(Fields.validFrom, referencedLineValidity.getFrom())
            .with(Fields.validTo, referencedLineValidity.getTo())
            .build()).build());
  }

}
