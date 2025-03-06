package ch.sbb.exportservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import ch.sbb.exportservice.exception.NotAllowedExportFileExceptionV2;
import ch.sbb.exportservice.model.ExportFilePathV2;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.service.FileExportService;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
class FileStreamingControllerApiV2Test {

  @Mock
  private FileExportService fileExportService;

  @InjectMocks
  private FileStreamingControllerApiV2 fileStreamingController;

  @BeforeEach
  void setUp() {
    try (var ignored = MockitoAnnotations.openMocks(this)) {
      log.info("Mocks are open");
    } catch (Exception ignored) {
    }
  }

  @Test
  void streamExportJsonFile() throws ExecutionException, InterruptedException {
    // Given
    InputStreamResource resource = new InputStreamResource(InputStream.nullInputStream());
    Mockito.when(fileExportService.streamJsonFile(any(ExportFilePathV2.class))).thenReturn(resource);

    // When
    CompletableFuture<ResponseEntity<InputStreamResource>> response = fileStreamingController.streamExportJsonFile(
        ExportObjectV2.LINE,
        ExportTypeV2.FULL
    );

    // Then
    assertThat(response.get().getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.get().getBody()).isEqualTo(resource);
    assertThat(response.get().getHeaders().getContentType()).isEqualTo(APPLICATION_JSON);
  }

  @Test
  void streamLatestExportJsonFile() throws ExecutionException, InterruptedException {
    // Given
    InputStreamResource resource = new InputStreamResource(InputStream.nullInputStream());
    Mockito.when(fileExportService.streamLatestJsonFile(any(ExportFilePathV2.class))).thenReturn(resource);

    // When
    CompletableFuture<ResponseEntity<InputStreamResource>> response = fileStreamingController.streamLatestExportJsonFile(
        ExportObjectV2.LINE,
        ExportTypeV2.FULL
    );

    // Then
    assertThat(response.get().getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.get().getBody()).isEqualTo(resource);
    assertThat(response.get().getHeaders().getContentType()).isEqualTo(APPLICATION_JSON);
  }

  @Test
  void streamExportGzFile() throws ExecutionException, InterruptedException {
    // Given
    InputStreamResource resource = new InputStreamResource(InputStream.nullInputStream());
    Mockito.when(fileExportService.streamGzipFile(any(String.class))).thenReturn(resource);

    // When
    CompletableFuture<ResponseEntity<InputStreamResource>> response = fileStreamingController.streamExportGzFile(
        ExportObjectV2.LINE,
        ExportTypeV2.FULL
    );

    // Then
    assertThat(response.get().getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.get().getBody()).isEqualTo(resource);
    assertThat(Objects.requireNonNull(response.get().getHeaders().getContentType()).toString()).isEqualTo("application/gzip");
  }

  @Test
  void streamLatestExportGzFile() throws ExecutionException, InterruptedException {
    // Given
    InputStreamResource resource = new InputStreamResource(InputStream.nullInputStream());
    Mockito.when(fileExportService.streamGzipFile(any(String.class))).thenReturn(resource);
    Mockito.when(fileExportService.getLatestUploadedFileName(any(ExportFilePathV2.class))).thenReturn("/test.json.gz");

    // When
    CompletableFuture<ResponseEntity<InputStreamResource>> response = fileStreamingController.streamLatestExportGzFile(
        ExportObjectV2.LINE,
        ExportTypeV2.FULL
    );

    // Then
    assertThat(response.get().getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.get().getBody()).isEqualTo(resource);
    assertThat(Objects.requireNonNull(response.get().getHeaders().getContentType()).toString()).isEqualTo("application/gzip");
  }

  @Test
  void notSupportedExport() {
    assertThrows(NotAllowedExportFileExceptionV2.class,
        () -> fileStreamingController.streamExportJsonFile(ExportObjectV2.LINE, ExportTypeV2.SWISS_ACTUAL));
  }

}
