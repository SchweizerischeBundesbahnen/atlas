package ch.sbb.timetable.field.number.exceptions;

import java.util.List;

public class ConflictException extends AtlasException {

  public ConflictException() {
    super(ExceptionCause.CONFLICT, List.of(""));
  }

}
