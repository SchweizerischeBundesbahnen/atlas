package ch.sbb.atlas.imports.bulk;

import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

public interface Validatable {

  @JsonIgnore
  List<BulkImportError> validate();

}
