package ch.sbb.importservice.module.bulkimport.writer;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.importservice.module.bulkimport.model.BulkImportConfig;
import java.util.List;
import java.util.function.Consumer;

public interface BulkImportItemWriter extends Consumer<List<BulkImportUpdateContainer<?>>> {

  BulkImportConfig getBulkImportConfig();
}
