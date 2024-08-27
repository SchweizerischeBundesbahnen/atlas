package ch.sbb.importservice.listener;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.importservice.entity.BulkImportLog;
import ch.sbb.importservice.repository.BulkImportLogRepository;
import ch.sbb.importservice.service.bulk.log.LogFile;
import ch.sbb.importservice.service.bulk.log.LogFile.LogEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@StepScope
@Component
@Transactional
@Slf4j
public class BulkImportDataValidationToLogFileListener implements ItemReadListener<BulkImportUpdateContainer<?>> {

  @Autowired
  private BulkImportLogRepository bulkImportLogRepository;
  @Autowired
  private ObjectMapper objectMapper;

  @Value("#{stepExecution}")
  private StepExecution stepExecution;

  @Override
  public void afterRead(@NotNull BulkImportUpdateContainer<?> item) {
    if (item.hasDataValidationErrors()) {
      bulkImportLogRepository.save(BulkImportLog.builder()
          .jobExecutionId(stepExecution.getJobExecution().getId())
          .lineNumber(item.getLineNumber())
          .logEntry(getLogEntry(item))
          .build());
    }
  }

  @SneakyThrows
  private String getLogEntry(BulkImportUpdateContainer<?> item) {
    LogEntry logEntry = LogFile.mapToDataValidationLogEntry(item);
    return objectMapper.writeValueAsString(logEntry);
  }
}
