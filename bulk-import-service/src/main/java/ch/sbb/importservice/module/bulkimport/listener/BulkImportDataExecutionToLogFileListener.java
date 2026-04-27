package ch.sbb.importservice.module.bulkimport.listener;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.importservice.module.bulkimport.log.BulkImportLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.ChunkListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@StepScope
@Component
@Transactional
@Slf4j
public class BulkImportDataExecutionToLogFileListener implements
    ChunkListener<BulkImportUpdateContainer<?>, BulkImportUpdateContainer<?>> {

  @Autowired
  private BulkImportLogService bulkImportLogService;

  @Value("#{stepExecution}")
  private StepExecution stepExecution;

  @Override
  public void afterChunk(Chunk<BulkImportUpdateContainer<?>> items) {
    log.info("Saving data execution log for {} items", items.getItems().size());
  }

}
