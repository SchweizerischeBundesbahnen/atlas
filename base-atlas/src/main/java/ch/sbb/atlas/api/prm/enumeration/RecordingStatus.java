package ch.sbb.atlas.api.prm.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum RecordingStatus {
  COMPLETE,
  INCOMPLETE
}
