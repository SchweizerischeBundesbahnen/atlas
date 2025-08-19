package ch.sbb.prm.directory.domain.stoppoint.service;

import ch.sbb.prm.directory.domain.stoppoint.entity.StopPointVersion;
import ch.sbb.prm.directory.domain.stoppoint.exception.StopPointVariantChangingNotAllowedException;
import ch.sbb.prm.directory.validation.RecordableVariantsValidationService;
import org.springframework.stereotype.Service;

@Service
public class StopPointValidationService extends RecordableVariantsValidationService<StopPointVersion> {

  @Override
  protected String getObjectName() {
    return StopPointVersion.class.getSimpleName();
  }

  public void validateStopPointRecordingVariants(StopPointVersion stopPointVersion){
    validateRecordingVariants(stopPointVersion , stopPointVersion.isReduced());
  }

  public void validateMeansOfTransportChanging(StopPointVersion currentVersion, StopPointVersion editedVersion){
    if(currentVersion.isReduced() != editedVersion.isReduced()){
      throw new StopPointVariantChangingNotAllowedException(currentVersion);
    }
  }
}
