package ch.sbb.atlas.wheelchairaccessibility.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlatformWithRelations {

  private final AccessibilityPlatform platform;
  private final List<? extends AccessibilityRelation> relations;

}
