package ch.sbb.line.directory.module.line.validation;

import ch.sbb.atlas.api.lidi.enumaration.v3.LineType;
import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.exception.RevokedException;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.entity.LineVersionV3.Fields;
import ch.sbb.line.directory.module.line.exception.LineV3FieldNotUpdatableException;
import org.springframework.stereotype.Service;

@Service
public class LineV3UpdateValidator {

  public void validateNotRevoked(LineVersionV3 current) {
    if (current.getStatus() == Status.REVOKED) {
      throw new RevokedException(current.getSlnid());
    }
  }

  public void validateConcessionFieldsUpdatable(LineVersionV3 current, LineVersionV3 edited) {
    boolean isMember = current.getReferencedLineSlnid() != null;
    if (isMember || current.getLineType() == LineType.ORDERLY) {
      return;
    }
    if (edited.getSwissLineNumber() != null) {
      throw new LineV3FieldNotUpdatableException(edited.getSwissLineNumber(), Fields.swissLineNumber, current.getLineType());
    }
    if (edited.getConcessionType() != null) {
      throw new LineV3FieldNotUpdatableException(edited.getConcessionType().name(), Fields.concessionType,
          current.getLineType());
    }
  }

}
