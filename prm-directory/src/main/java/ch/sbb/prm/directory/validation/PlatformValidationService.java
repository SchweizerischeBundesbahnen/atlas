package ch.sbb.prm.directory.validation;

import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.StopPointVersion;
import org.springframework.stereotype.Service;

@Service
public class PlatformValidationService extends RecordableVariantsValidationService<PlatformVersion> {

  @Override
  protected String getObjectName() {
    return PlatformVersion.class.getSimpleName();
  }

  public void validatePlatformRecordingVariants(PlatformVersion platformVersion, StopPointVersion stopPointVersion) {
    validateRecordingVariants(platformVersion, stopPointVersion.isReduced());
  }
}
