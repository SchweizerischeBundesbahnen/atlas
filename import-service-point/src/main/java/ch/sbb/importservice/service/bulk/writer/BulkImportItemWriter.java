package ch.sbb.importservice.service.bulk.writer;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.importservice.model.BulkImportConfig;
import java.util.function.Consumer;
import org.springframework.batch.item.Chunk;

public interface BulkImportItemWriter extends Consumer<Chunk<? extends BulkImportUpdateContainer<?>>> {

  BulkImportConfig getBulkImportConfig();
}
