package ch.sbb.atlas.user.administration.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class RestrictionWithoutTypeException extends AtlasException {

  private static final String ERROR = "Must provide type when including permissionRestrictions";

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message(ERROR)
        .error(ERROR)
        .details(new TreeSet<>())
        .build();
  }

}
