package ch.sbb.prm.directory.module.wheelchairaccessibility.mapper;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.ReadAccessibilityModel;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.ReadAccessibilityModel.AccessibilityRow;
import ch.sbb.atlas.wheelchairaccessibility.model.Accessibility;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AccessibilityMapper {

  public static ReadAccessibilityModel toModel(Accessibility accessibility) {
    return ReadAccessibilityModel.builder()
        .rows(accessibility.getAccessibilityInfos().stream()
            .map(accessibilityInfo -> AccessibilityRow.builder()
                .from(accessibilityInfo.getDateRange().getFrom())
                .to(accessibilityInfo.getDateRange().getTo())
                .accessibilityState(accessibilityInfo.getAccessibilityState())
                .build())
            .toList())
        .build();
  }
}
