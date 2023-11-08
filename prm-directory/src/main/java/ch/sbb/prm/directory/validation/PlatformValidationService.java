package ch.sbb.prm.directory.validation;

import ch.sbb.prm.directory.entity.PlatformVersion;
import org.springframework.stereotype.Service;

@Service
public class PlatformValidationService extends RecordableVariantsValidationService<PlatformVersion> {

  @Override
  protected String getObjectName() {
    return PlatformVersion.class.getSimpleName();
  }

}
