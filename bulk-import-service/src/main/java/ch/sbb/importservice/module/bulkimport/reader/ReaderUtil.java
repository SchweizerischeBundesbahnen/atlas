package ch.sbb.importservice.module.bulkimport.reader;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.Validatable;
import ch.sbb.importservice.module.bulkimport.service.BulkImportItemValidationService;
import ch.sbb.importservice.module.bulkimport.service.BulkImportValidationService;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReaderUtil {

  public static <T extends Validatable<T>> List<BulkImportUpdateContainer<T>> readAndValidate(File file,
      Class<T> clazz) {
    List<BulkImportUpdateContainer<T>> csvLines = BulkImportCsvReader.readLinesFromFileWithNullingValue(file, clazz);

    BulkImportItemValidationService.validateAll(csvLines);
    BulkImportValidationService.validateUniqueness(csvLines);

    return new ArrayList<>(csvLines);
  }

}
