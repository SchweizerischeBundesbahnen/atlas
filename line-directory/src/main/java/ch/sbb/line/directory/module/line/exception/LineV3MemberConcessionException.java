package ch.sbb.line.directory.module.line.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.line.directory.module.line.entity.LineVersionV3.Fields;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.http.HttpStatus;

/**
 * Member lines: concessionType and swissLineNumber are mandatory for LineType CONCESSION and forbidden otherwise.
 */
public class LineV3MemberConcessionException extends AtlasException {

  private static final String CODE_CONCESSION_TYPE = "LIDI.SUBLINE.ERROR.CONCESSION";
  private static final String CODE_SWISS_LINE_NUMBER = "LIDI.SUBLINE.ERROR.SWISS_SUBLINE_NUMBER";

  private final String field;
  private final String code;
  private final String message;

  private LineV3MemberConcessionException(String field, String code, String message) {
    this.field = field;
    this.code = code;
    this.message = message;
  }

  public static LineV3MemberConcessionException concessionType() {
    return new LineV3MemberConcessionException(Fields.concessionType, CODE_CONCESSION_TYPE,
        "ConcessionType only allowed on LineType CONCESSION, but then mandatory");
  }

  public static LineV3MemberConcessionException swissLineNumber() {
    return new LineV3MemberConcessionException(Fields.swissLineNumber, CODE_SWISS_LINE_NUMBER,
        "SwissLineNumber only allowed on LineType CONCESSION, but then mandatory");
  }

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message(message)
        .error(message)
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private Set<Detail> getErrorDetails() {
    return Set.of(Detail.builder()
        .field(field)
        .message(message)
        .displayInfo(builder().code(code).build()).build());
  }

}
