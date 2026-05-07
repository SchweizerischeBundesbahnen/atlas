package ch.sbb.prm.directory.module.wheelchairaccessibility.helper;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.module.relation.entity.RelationVersion;
import ch.sbb.prm.directory.module.relation.repository.RelationRepository;
import ch.sbb.prm.directory.module.stoppoint.entity.StopPointVersion;
import ch.sbb.prm.directory.module.stoppoint.repository.StopPointRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WheelchairAccessibilityDataLoader {

  private final RelationRepository relationRepository;
  private final StopPointRepository stopPointRepository;

  public StopPointVersion loadStopPointValidToday(String sloid) {
    return stopPointRepository.findAllBySloidOrderByValidFrom(sloid)
        .stream()
        .filter(v -> ValidityHelper.isValidToday(v.getValidFrom(), v.getValidTo()))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No StopPoint version valid today for sloid: " + sloid));
  }

  public List<RelationVersion> loadRelationsValidToday(String platformSloid) {
    return relationRepository
        .findAllBySloidAndReferencePointElementType(platformSloid, ReferencePointElementType.PLATFORM)
        .stream()
        .filter(r -> ValidityHelper.isValidToday(r.getValidFrom(), r.getValidTo()))
        .toList();
  }
}
