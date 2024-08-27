package ch.sbb.importservice.listener;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.importservice.entity.BulkImportLog;
import ch.sbb.importservice.repository.BulkImportLogRepository;
import ch.sbb.importservice.service.bulk.log.LogFile;
import ch.sbb.importservice.service.bulk.log.LogFile.LogEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@StepScope
@Component
@Transactional
@Slf4j
public class BulkImportDataExecutionToLogFileListener implements ItemWriteListener<BulkImportUpdateContainer<?>> {

  @Autowired
  private BulkImportLogRepository bulkImportLogRepository;
  @Autowired
  private ObjectMapper objectMapper;

  @Value("#{stepExecution}")
  private StepExecution stepExecution;

  @Override
  public void afterWrite(Chunk<? extends BulkImportUpdateContainer<?>> items) {
    items.getItems().forEach(writeItem ->
        bulkImportLogRepository.save(BulkImportLog.builder()
        .jobExecutionId(stepExecution.getJobExecution().getId())
        .lineNumber(writeItem.getLineNumber())
        .logEntry(getLogEntry(writeItem))
        .build()));
  }


  @SneakyThrows
  private String getLogEntry(BulkImportUpdateContainer<?> item) {
    LogEntry logEntry = LogFile.mapToDataExecutionLogEntry(item);
    return objectMapper.writeValueAsString(logEntry);
  }
}
