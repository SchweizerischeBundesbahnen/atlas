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
            .map(i -> AccessibilityRow.builder().dateRange(i.getDateRange()).accessibilityState(i.getAccessibilityState())
                .build())
            .toList())
        .build();
  }
}
