package ch.sbb.atlas.servicepointdirectory.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true)
@RequiredArgsConstructor
public enum ServicePointStatus {

  TO_BE_REQUESTED(0, "To be requested"),
  REQUESTED(1, "Requested"),
  PLANNED(2, "Planned"),
  IN_OPERATION(3, "In operation"),
  TERMINATED(4, "Terminated"),
  IN_POST_OPERATIONAL_PHASE(5, "In post-operational phase"),
  HISTORICAL(6, "Historical"),
  UNKNOWN(7, "unknown");

  private final Integer id;
  private final String designation;

  public static ServicePointStatus from(Integer id) {
    return Arrays.stream(ServicePointStatus.values()).filter(el -> Objects.equals(el.id, id)).findFirst().orElse(UNKNOWN);
  }
}
