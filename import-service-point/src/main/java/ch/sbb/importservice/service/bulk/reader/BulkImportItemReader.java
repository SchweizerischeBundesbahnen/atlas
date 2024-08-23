package ch.sbb.importservice.service.bulk.reader;

import ch.sbb.atlas.imports.bulk.BulkImportContainer;
import ch.sbb.importservice.model.BulkImportConfig;
import java.io.File;
import java.util.List;
import java.util.function.Function;

public interface BulkImportItemReader extends Function<File, List<BulkImportContainer>> {

  BulkImportConfig getBulkImportConfig();

  Class<?> getCsvModelClass();

}
