package ch.sbb.importservice.service;

import ch.sbb.atlas.imports.BulkImportContainer;
import ch.sbb.importservice.model.BulkImportConfig;
import java.io.File;
import java.util.List;
import java.util.function.Function;

public interface BulkImportItemReader extends Function<File, List<BulkImportContainer>> {

  BulkImportConfig getBulkImportConfig();
}
