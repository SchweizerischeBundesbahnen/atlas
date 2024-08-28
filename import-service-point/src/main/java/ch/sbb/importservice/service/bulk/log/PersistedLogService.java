package ch.sbb.importservice.service.bulk.log;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.importservice.entity.BulkImport;
import ch.sbb.importservice.entity.BulkImportLog;
import ch.sbb.importservice.repository.BulkImportLogRepository;
import ch.sbb.importservice.service.bulk.log.LogFile.LogEntry;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersistedLogService {

  private final BulkImportLogRepository bulkImportLogRepository;
  private final ObjectMapper objectMapper;
  private final FileService fileService;

  public void saveDataValidationErrors(Long jobExecutionId, BulkImportUpdateContainer<?> item) {
    if (item.hasDataValidationErrors()) {
      LogEntry logEntry = LogFile.mapToDataValidationLogEntry(item);
      bulkImportLogRepository.save(BulkImportLog.builder()
          .jobExecutionId(jobExecutionId)
          .lineNumber(item.getLineNumber())
          .logEntry(mapToJsonString(logEntry))
          .build());
    }
  }

  public void saveDataExecutionLog(Long jobExecutionId, BulkImportUpdateContainer<?> item) {
    if (!item.hasDataValidationErrors()) {
      LogEntry logEntry = LogFile.mapToDataExecutionLogEntry(item);
      bulkImportLogRepository.save(BulkImportLog.builder()
          .jobExecutionId(jobExecutionId)
          .lineNumber(item.getLineNumber())
          .logEntry(mapToJsonString(logEntry))
          .build());
    }
  }

  public LogFile getLogFile(Long jobExecutionId) {
    List<BulkImportLog> log = bulkImportLogRepository.findAllByJobExecutionId(jobExecutionId);
    List<LogEntry> logEntries = log.stream()
        .map(i -> mapToLogEntry(i.getLogEntry()))
        .sorted(Comparator.comparing(LogEntry::getLineNumber))
        .toList();
    return LogFile.builder()
        .logEntries(logEntries)
        .build();
  }

  @SneakyThrows
  private String mapToJsonString(LogEntry logEntry) {
    return objectMapper.writeValueAsString(logEntry);
  }

  @SneakyThrows
  private LogEntry mapToLogEntry(String logEntryAsJson) {
    return objectMapper.readValue(logEntryAsJson, LogEntry.class);
  }

  @SneakyThrows
  public File writeLogToFile(LogFile logFile, BulkImport currentImport) {
    ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
    String fileName = "%s_%s_%s.log".formatted(currentImport.getObjectType(), currentImport.getImportType(),
        currentImport.getId());
    File file = new File(fileService.getDir() + File.separator + fileName);
    writer.writeValue(file, logFile);
    return file;
  }
}
