package ch.sbb.prm.directory.exception;

import java.util.Map;
import lombok.Getter;

@Getter
public class PlatformRecordingVariantException extends RecordingVariantException {

  private static final String ERROR = "StopPoint precondition failed";

  public PlatformRecordingVariantException(Map<String, String> errorConstraintMap, String objectName) {
    super(errorConstraintMap, objectName);
  }

}
