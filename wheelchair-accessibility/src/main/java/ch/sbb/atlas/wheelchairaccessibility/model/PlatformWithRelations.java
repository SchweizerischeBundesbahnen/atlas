package ch.sbb.atlas.wheelchairaccessibility.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlatformWithRelations {

  private final AccessibilityPlatform platform;

  @Builder.Default
  private final List<? extends AccessibilityRelation> relations = new ArrayList<>();

}
