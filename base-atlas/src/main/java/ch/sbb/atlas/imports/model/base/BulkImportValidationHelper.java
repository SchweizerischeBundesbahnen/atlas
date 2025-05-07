package ch.sbb.atlas.imports.model.base;

import ch.sbb.atlas.imports.bulk.BulkImportErrors;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BulkImportValidationHelper {

  public static void validateServicePointNumber(
      Integer number,
      List<BulkImportError> errors
  ) {
    if (number != null) {
      try {
        ServicePointNumber.ofNumberWithoutCheckDigit(number);
      } catch (Exception e) {
        errors.add(BulkImportErrors.invalidServicePointNumber());
      }
    }
  }
}
