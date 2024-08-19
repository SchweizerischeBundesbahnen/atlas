package ch.sbb.importservice.writer;

import ch.sbb.atlas.imports.bulk.BulkImportContainer;
import ch.sbb.importservice.model.BulkImportConfig;
import java.util.function.Consumer;
import org.springframework.batch.item.Chunk;

public interface BulkImportItemWriter extends Consumer<Chunk<? extends BulkImportContainer>> {

  BulkImportConfig getBulkImportConfig();
}
