package ch.sbb.atlas.kafka.model.user.admin;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Gets serialized to kafka with class and package-name. Do care.
 */
@Schema(enumAsRef = true)
public enum ApplicationRole {
  READER,
  WRITER,
  SUPER_USER,
  SUPERVISOR
}
