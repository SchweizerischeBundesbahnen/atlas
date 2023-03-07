package ch.sbb.atlas.kafka.model.user.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Gets serialized to kafka with class and package-name. Do care.
 */
@Schema(enumAsRef = true)
@Getter
@RequiredArgsConstructor
public enum ApplicationRole {
  READER(1),
  EXPLICIT_READER(2),
  WRITER(3),
  SUPER_USER(4),
  SUPERVISOR(5)

  ;

  private final int rank;
}
