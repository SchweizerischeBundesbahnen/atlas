package ch.sbb.prm.directory.module.referencepoint.service;

import ch.sbb.prm.directory.module.referencepoint.entity.ReferencePointVersion;
import ch.sbb.prm.directory.module.referencepoint.exception.MainReferencePointConflictException;
import ch.sbb.prm.directory.module.referencepoint.repository.ReferencePointRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReferencePointValidationService {

  private final ReferencePointRepository referencePointRepository;

  public ReferencePointValidationService(ReferencePointRepository referencePointRepository) {
    this.referencePointRepository = referencePointRepository;
  }

  public void validatePreconditionBusinessRule(ReferencePointVersion referencePointVersion) {
    validateMainReferencePointConflict(referencePointVersion);
  }

  private void validateMainReferencePointConflict(ReferencePointVersion referencePointVersion) {
    if (referencePointVersion.getMainReferencePoint()) {
      List<ReferencePointVersion> mainReferencePointOverlaps = referencePointRepository.findMainReferencePointOverlaps(
          referencePointVersion);
      if (!mainReferencePointOverlaps.isEmpty()) {
        throw new MainReferencePointConflictException(mainReferencePointOverlaps);
      }
    }
  }
}
