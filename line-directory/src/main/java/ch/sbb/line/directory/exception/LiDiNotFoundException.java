package ch.sbb.line.directory.exception;

import ch.sbb.atlas.model.exception.NotFoundException;
import ch.sbb.line.directory.entity.LineVersion.Fields;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class LiDiNotFoundException {

  public static class SlnidNotFoundException extends NotFoundException {

    public SlnidNotFoundException(String value) {
      super(Fields.slnid, value);
    }
  }

  public static class TtfnidNotFoundException extends NotFoundException {

    public TtfnidNotFoundException(String value) {
      super(TimetableFieldNumberVersion.Fields.ttfnid, value);
    }
  }
}
