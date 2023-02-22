package ch.sbb.atlas.timetable.hearing.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum StatementStatus {
  RECEIVED,
  IN_REVIEW,
  ACCEPTED,
  REJECTED,
  MOVED,
  JUNK
}
