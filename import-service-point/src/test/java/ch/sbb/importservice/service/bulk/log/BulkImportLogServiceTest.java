package ch.sbb.importservice.service.bulk.log;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.importservice.repository.BulkImportLogRepository;
import ch.sbb.importservice.service.bulk.BulkImportS3BucketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BulkImportLogServiceTest {

  private BulkImportLogService bulkImportLogService;
  private BulkImportS3BucketService bulkImportS3BucketService;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    BulkImportLogRepository bulkImportLogRepository = Mockito.mock(BulkImportLogRepository.class);
    bulkImportS3BucketService = Mockito.mock(BulkImportS3BucketService.class);
    objectMapper = Mockito.mock(ObjectMapper.class);
    FileService fileService = Mockito.mock(FileService.class);
    bulkImportLogService = new BulkImportLogService(bulkImportLogRepository, bulkImportS3BucketService, objectMapper,
        fileService);
  }

  @Test
  void getLogFileFromS3() throws IOException {
    File file = new File("/test.log");
    Mockito.when(bulkImportS3BucketService.downloadImportFile("/test.log")).thenReturn(file);
    Mockito.when(objectMapper.readValue(file, LogFile.class)).thenReturn(LogFile.builder()
        .nbOfSuccess(5L)
        .build());

    LogFile logFile = bulkImportLogService.getLogFileFromS3("/test.log");

    assertThat(logFile.getNbOfSuccess()).isEqualTo(5);
  }

  @Test
  void shouldThrowRuntimeExceptionWhenFileDoesNotExistOrIsInvalid() throws IOException {
    Mockito.when(bulkImportS3BucketService.downloadImportFile("/test.log")).thenReturn(null);
    Mockito.when(objectMapper.readValue((File) null, LogFile.class)).thenThrow(IOException.class);

    assertThrows(RuntimeException.class, () -> bulkImportLogService.getLogFileFromS3("/test.log"),
        "Unexpected exception during parsing of Bulk Import Result Log File!");
  }
}
