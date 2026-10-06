package ch.sbb.exportservice.job.prm.ersad.accessibility;

import ch.sbb.exportservice.job.prm.ersad.model.NetexTriState;
import ch.sbb.exportservice.job.prm.platform.entity.PlatformVersion;
import ch.sbb.exportservice.job.prm.relation.entity.RelationVersion;
import ch.sbb.exportservice.job.prm.stoppoint.entity.StopPointVersion;
import java.time.LocalDate;
import java.util.List;

public interface ErsadAccessibilityDeriver {

  NetexTriState derive(StopPointVersion stopPoint, List<PlatformVersion> platforms,
      List<RelationVersion> relations, LocalDate date);
}
