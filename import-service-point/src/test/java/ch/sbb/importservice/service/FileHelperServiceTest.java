package ch.sbb.importservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import java.io.File;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

 class FileHelperServiceTest {

  private FileHelperService fileHelperService;

  @Mock
  private AmazonService amazonService;

  @Mock
  private FileService fileService;

  @BeforeEach
   void init() {
    openMocks(this);
    fileHelperService = new FileHelperService(amazonService, fileService);
  }

  @Test
  void shouldNotFoundFileToDownload() {
    //given
    String today = LocalDate.now().toString().replaceAll("-", "");
    when(amazonService.getS3ObjectKeysFromPrefix(eq(AmazonBucket.EXPORT), eq("servicepoint_didok"), eq("PREFIX_FILE"))).thenReturn(
        Collections.emptyList());

    //when & then
    String exMessage =
        assertThrows(RuntimeException.class,
            () -> fileHelperService.downloadImportFileFromS3("PREFIX_FILE")).getLocalizedMessage();
    verify(amazonService).getS3ObjectKeysFromPrefix(eq(AmazonBucket.EXPORT), eq("servicepoint_didok"), eq("PREFIX_FILE"));
    assertThat(exMessage).isEqualTo("[IMPORT]: File PREFIX_FILE not found on S3");
  }

  @Test
  void shouldFindMoreThanOneFileToDownload() {
    //given
    String today = LocalDate.now().toString().replaceAll("-", "");
    when(amazonService.getS3ObjectKeysFromPrefix(eq(AmazonBucket.EXPORT), eq("servicepoint_didok"), eq("PREFIX_FILE")))
        .thenReturn(List.of("file1", "file2"));

    //when & then
    String exMessage =
        assertThrows(RuntimeException.class,
            () -> fileHelperService.downloadImportFileFromS3("PREFIX_FILE")).getLocalizedMessage();
    verify(amazonService).getS3ObjectKeysFromPrefix(eq(AmazonBucket.EXPORT), eq("servicepoint_didok"), eq("PREFIX_FILE"));
    assertThat(exMessage).isEqualTo("[IMPORT]: Found more than 1 file PREFIX_FILE to download on S3");
  }

  @Test
  void shouldDownloadJustOneFile() {
    //given
    String today = LocalDate.now().toString().replaceAll("-", "");
    when(amazonService.getS3ObjectKeysFromPrefix(eq(AmazonBucket.EXPORT), eq("servicepoint_didok"), eq("PREFIX_FILE"))).thenReturn(List.of("file"));
    when(amazonService.pullFile(eq(AmazonBucket.EXPORT), eq("file"))).thenReturn(new File("file"));

    //when
    File file = fileHelperService.downloadImportFileFromS3("PREFIX_FILE");

    //then
    verify(amazonService).getS3ObjectKeysFromPrefix(eq(AmazonBucket.EXPORT), eq("servicepoint_didok"), eq("PREFIX_FILE"));
    verify(amazonService).pullFile(eq(AmazonBucket.EXPORT), eq("file"));
    assertThat(file.getName()).isEqualTo("file");
  }
}