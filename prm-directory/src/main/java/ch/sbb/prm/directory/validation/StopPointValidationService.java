package ch.sbb.prm.directory.validation;

import ch.sbb.prm.directory.entity.StopPointVersion;
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
}
