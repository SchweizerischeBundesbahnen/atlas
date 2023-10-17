package ch.sbb.atlas.api.prm.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true, example = "PLATFORM_ACCESS_WITHOUT_ASSISTANCE")
@Getter
@RequiredArgsConstructor
public enum VehicleAccessAttributeType {
  TO_BE_COMPLETED(0),
  PLATFORM_ACCESS_WITHOUT_ASSISTANCE(11),
  PLATFORM_ACCESS_WITH_ASSISTANCE(12),
  PLATFORM_ACCESS_WITH_ASSISTANCE_WHEN_NOTIFIED(13),
  PLATFORM_NOT_WHEELCHAIR_ACCESSIBLE(14);

  private final Integer rank;

}
