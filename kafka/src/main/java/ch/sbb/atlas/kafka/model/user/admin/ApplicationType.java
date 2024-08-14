package ch.sbb.atlas.kafka.model.user.admin;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Gets serialized to kafka with class and package-name. Do care.
 */
@Schema(enumAsRef = true)
public enum ApplicationType {

  TTFN,
  LIDI,
  BODI,
  TIMETABLE_HEARING,
  SEPODI,
  PRM

}
