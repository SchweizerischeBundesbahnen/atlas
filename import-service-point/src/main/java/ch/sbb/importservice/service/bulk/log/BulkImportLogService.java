package ch.sbb.importservice.service.bulk.log;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportStatus;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.importservice.entity.BulkImport;
import ch.sbb.importservice.entity.BulkImportLog;
import ch.sbb.importservice.repository.BulkImportLogRepository;
import ch.sbb.importservice.service.bulk.BulkImportS3BucketService;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BulkImportLogService {

  private final BulkImportLogRepository bulkImportLogRepository;
  private final BulkImportS3BucketService bulkImportS3BucketService;
  private final ObjectMapper objectMapper;
  private final FileService fileService;

  public void saveDataValidationErrors(Long jobExecutionId, BulkImportUpdateContainer<?> item) {
    if (item.hasDataValidationErrors()) {
      bulkImportLogRepository.save(BulkImportLog.builder()
          .jobExecutionId(jobExecutionId)
          .lineNumber(item.getLineNumber())
          .logEntry(mapToJsonString(item.getBulkImportLogEntry()))
          .build());
    }
  }

  public void saveDataExecutionLog(Long jobExecutionId, BulkImportUpdateContainer<?> item) {
    if (!item.hasDataValidationErrors()) {
      bulkImportLogRepository.save(BulkImportLog.builder()
          .jobExecutionId(jobExecutionId)
          .lineNumber(item.getLineNumber())
          .logEntry(mapToJsonString(item.getBulkImportLogEntry()))
          .build());
    }
  }

  public LogFile getLogFile(Long jobExecutionId) {
    List<BulkImportLog> log = bulkImportLogRepository.findAllByJobExecutionId(jobExecutionId);
    List<BulkImportLogEntry> logEntries = log.stream()
        .map(i -> mapToLogEntry(i.getLogEntry()))
        .sorted(Comparator.comparing(BulkImportLogEntry::getLineNumber))
        .toList();
    Map<BulkImportStatus, Long> statusCounts = logEntries.stream().collect(Collectors.groupingBy(i -> i.getStatus(),
        Collectors.counting()));
    return LogFile.builder()
        .nbOfSuccess(statusCounts.getOrDefault(BulkImportStatus.SUCCESS, 0L))
        .nbOfInfo(statusCounts.getOrDefault(BulkImportStatus.INFO, 0L))
        .nbOfError(statusCounts.getOrDefault(BulkImportStatus.DATA_EXECUTION_ERROR, 0L) + statusCounts.getOrDefault(
            BulkImportStatus.DATA_VALIDATION_ERROR, 0L))
        .logEntries(logEntries.stream().filter(i -> i.getStatus() != BulkImportStatus.SUCCESS).toList())
        .build();
  }

  public LogFile getLogFileFromS3(String logFileUrl) {
    File logFile = bulkImportS3BucketService.downloadImportFile(logFileUrl);
    try {
      return objectMapper.readValue(logFile, LogFile.class);
    } catch (IOException e) {
      throw new RuntimeException("Unexpected exception during parsing of Bulk Import Result Log File!", e);
    }
  }

  @SneakyThrows
  private String mapToJsonString(BulkImportLogEntry logEntry) {
    return objectMapper.writeValueAsString(logEntry);
  }

  @SneakyThrows
  private BulkImportLogEntry mapToLogEntry(String logEntryAsJson) {
    return objectMapper.readValue(logEntryAsJson, BulkImportLogEntry.class);
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

  public void deleteLog(Long jobExecutionId) {
    bulkImportLogRepository.deleteAllByJobExecutionId(jobExecutionId);
  }
}
