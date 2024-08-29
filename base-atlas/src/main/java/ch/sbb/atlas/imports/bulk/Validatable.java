package ch.sbb.atlas.imports.bulk;

import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import java.util.List;

public interface Validatable {

  List<BulkImportError> validate();

}
