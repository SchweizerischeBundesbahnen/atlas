package ch.sbb.timetable.field.number.exceptions;

import ch.sbb.timetable.field.number.api.ErrorResponse;

public abstract class AtlasException extends RuntimeException {

  public abstract ErrorResponse getErrorResponse();

}
