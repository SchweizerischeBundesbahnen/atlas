package ch.sbb.timetable.field.number.exceptions;

import ch.sbb.timetable.field.number.entity.Version;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConflictException extends AtlasException {

  private final Version newVersion;
  private final List<Version> overlappingVersions;

  @Override
  public List<String> getMessageParameters() {
    // TODO: Parameter und Message finalisieren
    return Collections.emptyList();
  }

  public ExceptionCause getExceptionCause(){
    // TODO: Auswertung der Überlappung
    return ExceptionCause.NUMBER_CONFLICT;
  }
}
