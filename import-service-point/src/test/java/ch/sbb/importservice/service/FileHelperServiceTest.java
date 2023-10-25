package ch.sbb.importservice.service;

import static ch.sbb.importservice.service.csv.CsvFileNameModel.SERVICEPOINT_DIDOK_DIR_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.importservice.service.csv.CsvFileNameModel;
import ch.sbb.importservice.service.csv.TrafficPointCsvService;
import java.io.File;
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

  private final CsvFileNameModel csvFileNameModel = CsvFileNameModel.builder()
      .fileName(TrafficPointCsvService.TRAFFIC_POINT_FILE_PREFIX)
      .s3BucketDir(SERVICEPOINT_DIDOK_DIR_NAME)
      .addDateToPostfix(false)
      .build();

  @Test
  void shouldNotFoundFileToDownload() {
    //given
    when(
        amazonService.getS3ObjectKeysFromPrefix(AmazonBucket.EXPORT, csvFileNameModel.getS3BucketDir(),
            csvFileNameModel.getFileName())).thenReturn(
        Collections.emptyList());

    //when & then
    String exMessage =
        assertThrows(RuntimeException.class,
            () -> fileHelperService.downloadImportFileFromS3(csvFileNameModel)).getLocalizedMessage();
    verify(amazonService).getS3ObjectKeysFromPrefix(AmazonBucket.EXPORT, csvFileNameModel.getS3BucketDir(),
        csvFileNameModel.getFileName());
    assertThat(exMessage).isEqualTo("[IMPORT]: File "+ csvFileNameModel.getFileName()+" not found on S3");
  }

  @Test
  void shouldFindMoreThanOneFileToDownload() {
    //given
    when(amazonService.getS3ObjectKeysFromPrefix(AmazonBucket.EXPORT, csvFileNameModel.getS3BucketDir(), csvFileNameModel.getFileName()))
        .thenReturn(List.of("file1", "file2"));

    //when & then
    String exMessage =
        assertThrows(RuntimeException.class,
            () -> fileHelperService.downloadImportFileFromS3(csvFileNameModel)).getLocalizedMessage();
    verify(amazonService).getS3ObjectKeysFromPrefix(AmazonBucket.EXPORT, csvFileNameModel.getS3BucketDir(), csvFileNameModel.getFileName());
    assertThat(exMessage).isEqualTo("[IMPORT]: Found more than 1 file " + csvFileNameModel.getFileName()+ " to download on S3");
  }

  @Test
  void shouldDownloadJustOneFile() {
    //given
    when(
        amazonService.getS3ObjectKeysFromPrefix(AmazonBucket.EXPORT, csvFileNameModel.getS3BucketDir(),
            csvFileNameModel.getFileName())).thenReturn(
        List.of("file"));
    when(amazonService.pullFile(AmazonBucket.EXPORT, "file")).thenReturn(new File("file"));

    //when
    File file = fileHelperService.downloadImportFileFromS3(csvFileNameModel);

    //then
    verify(amazonService).getS3ObjectKeysFromPrefix(AmazonBucket.EXPORT, csvFileNameModel.getS3BucketDir(), csvFileNameModel.getFileName());
    verify(amazonService).pullFile(eq(AmazonBucket.EXPORT), eq("file"));
    assertThat(file.getName()).isEqualTo("file");
  }
}