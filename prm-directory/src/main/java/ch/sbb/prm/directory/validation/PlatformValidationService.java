package ch.sbb.prm.directory.validation;

import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.exception.AttentionFieldMeanOfTransportConflictException;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class PlatformValidationService extends RecordableVariantsValidationService<PlatformVersion> {

  @Override
  protected String getObjectName() {
    return PlatformVersion.class.getSimpleName();
  }

  public void validatePreconditions(PlatformVersion version, Set<MeanOfTransport> meanOfTransports) {
    boolean attentionFieldMandatory = PrmMeansOfTransportHelper.isAttentionFieldAllowed(meanOfTransports);

    if (attentionFieldMandatory ^ version.getAttentionField() != null) {
      throw new AttentionFieldMeanOfTransportConflictException();
    }
  }
}
