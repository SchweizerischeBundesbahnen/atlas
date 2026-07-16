package ch.sbb.line.directory.module.line.validation;

import ch.sbb.atlas.api.lidi.UpdateLineVersionModelV2.Fields;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.line.directory.module.line.entity.LineVersion;
import ch.sbb.line.directory.module.line.exception.LineFieldNotUpdatableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LineUpdateValidationService {


  public void validateFieldsNotUpdatableForLineTypeOrderly(LineVersion currentVersion, LineVersion editedVersion) {
    if (currentVersion.getLineType() != LineType.ORDERLY) {
      if (editedVersion.getSwissLineNumber() != null) {
        throw new LineFieldNotUpdatableException(editedVersion.getSwissLineNumber(), Fields.swissLineNumber,
            currentVersion.getLineType());
      }
      if (editedVersion.getConcessionType() != null) {
        throw new LineFieldNotUpdatableException(editedVersion.getConcessionType().name(), Fields.lineConcessionType,
            currentVersion.getLineType());
      }
    }
  }

}
