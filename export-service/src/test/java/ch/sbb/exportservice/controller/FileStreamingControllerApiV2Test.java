package ch.sbb.exportservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import ch.sbb.atlas.s3.service.ContentLength;
import ch.sbb.atlas.s3.service.StreamedFile;
import ch.sbb.exportservice.exception.NotAllowedExportExtensionExceptionV2;
import ch.sbb.exportservice.exception.NotAllowedExportFileExceptionV2;
import ch.sbb.exportservice.model.ExportFilePathV2;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.service.FileExportService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Slf4j
class FileStreamingControllerApiV2Test {

  @Mock
  private FileExportService fileExportService;

  @InjectMocks
  private FileStreamingControllerApiV2 fileStreamingController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void streamExportJsonFile() throws ExecutionException, InterruptedException {
    // Given
    InputStreamResource resource = new InputStreamResource(InputStream.nullInputStream());
    when(fileExportService.streamJsonFile(any(ExportFilePathV2.class))).thenReturn(new StreamedFile(resource, ContentLength.of(42L)));

    // When
    CompletableFuture<ResponseEntity<InputStreamResource>> response = fileStreamingController.streamExportJsonFile(
        ExportObjectV2.LINE,
        ExportTypeV2.FULL
    );

    // Then
    assertThat(response.get().getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.get().getBody()).isEqualTo(resource);
    assertThat(response.get().getHeaders().getContentType()).isEqualTo(APPLICATION_JSON);
    assertThat(response.get().getHeaders().getContentLength()).isEqualTo(42L);
  }

  @Test
  void streamLatestExportJsonFile() throws ExecutionException, InterruptedException {
    // Given
    InputStreamResource resource = new InputStreamResource(InputStream.nullInputStream());
    when(fileExportService.streamLatestJsonFile(any(ExportFilePathV2.class)))
        .thenReturn(new StreamedFile(resource, ContentLength.of(42L)));

    // When
    CompletableFuture<ResponseEntity<InputStreamResource>> response = fileStreamingController.streamLatestExportJsonFile(
        ExportObjectV2.LINE,
        ExportTypeV2.FULL
    );

    // Then
    assertThat(response.get().getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.get().getBody()).isEqualTo(resource);
    assertThat(response.get().getHeaders().getContentType()).isEqualTo(APPLICATION_JSON);
    assertThat(response.get().getHeaders().getContentLength()).isEqualTo(42L);
  }

  @Test
  void streamExportGzFile() throws ExecutionException, InterruptedException {
    // Given
    InputStreamResource resource = new InputStreamResource(InputStream.nullInputStream());
    when(fileExportService.streamGzipFile(any(String.class))).thenReturn(new StreamedFile(resource, ContentLength.of(42L)));

    // When
    CompletableFuture<ResponseEntity<InputStreamResource>> response = fileStreamingController.streamExportGzFile(
        ExportObjectV2.LINE,
        ExportTypeV2.FULL
    );

    // Then
    assertThat(response.get().getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.get().getBody()).isEqualTo(resource);
    assertThat(Objects.requireNonNull(response.get().getHeaders().getContentType())).hasToString("application"
        + "/gzip");
    assertThat(response.get().getHeaders().getContentLength()).isEqualTo(42L);
  }

  @Test
  void streamLatestExportGzFile() throws ExecutionException, InterruptedException {
    // Given
    InputStreamResource resource = new InputStreamResource(InputStream.nullInputStream());
    when(fileExportService.streamGzipFile(any(String.class))).thenReturn(new StreamedFile(resource, ContentLength.of(42L)));
    when(fileExportService.getLatestUploadedFileName(any(ExportFilePathV2.class))).thenReturn("/test.json.gz");

    // When
    CompletableFuture<ResponseEntity<InputStreamResource>> response = fileStreamingController.streamLatestExportGzFile(
        ExportObjectV2.LINE,
        ExportTypeV2.FULL
    );

    // Then
    assertThat(response.get().getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.get().getBody()).isEqualTo(resource);
    assertThat(Objects.requireNonNull(response.get().getHeaders().getContentType())).hasToString("application/gzip");
    assertThat(response.get().getHeaders().getContentLength()).isEqualTo(42L);
  }

  @Test
  void notSupportedExport() {
    assertThrows(NotAllowedExportFileExceptionV2.class,
        () -> fileStreamingController.streamExportJsonFile(ExportObjectV2.LINE, ExportTypeV2.SWISS_ACTUAL));
  }

  @Test
  void shouldStreamExportXmlFile() throws ExecutionException, InterruptedException, IOException {
    // Given
    StreamingResponseBody streamingResponseBody = outputStream -> outputStream.write(
        "<xml/>".getBytes(StandardCharsets.UTF_8));
    when(fileExportService.streamXmlFile(any(ExportFilePathV2.class))).thenReturn(streamingResponseBody);

    // When
    CompletableFuture<ResponseEntity<StreamingResponseBody>> response = fileStreamingController.streamExportXmlFile(
        ExportObjectV2.ERSAD_NETEX,
        ExportTypeV2.ACTUAL
    );

    // Then
    assertThat(response.get().getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_XML);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Objects.requireNonNull(response.get().getBody()).writeTo(outputStream);
    assertThat(outputStream.toString(StandardCharsets.UTF_8)).isEqualTo("<xml/>");
  }

  @Test
  void shouldStreamLatestExportXmlFile() throws ExecutionException, InterruptedException, IOException {
    // Given
    StreamingResponseBody streamingResponseBody = outputStream -> outputStream.write(
        "<xml/>".getBytes(StandardCharsets.UTF_8));
    when(fileExportService.streamLatestXmlFile(any(ExportFilePathV2.class))).thenReturn(streamingResponseBody);

    // When
    CompletableFuture<ResponseEntity<StreamingResponseBody>> response = fileStreamingController.streamLatestExportXmlFile(
        ExportObjectV2.ERSAD_NETEX,
        ExportTypeV2.ACTUAL
    );

    // Then
    assertThat(response.get().getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_XML);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Objects.requireNonNull(response.get().getBody()).writeTo(outputStream);
    assertThat(outputStream.toString(StandardCharsets.UTF_8)).isEqualTo("<xml/>");
  }

  @Test
  void shouldStreamExportZipXmlFile() throws ExecutionException, InterruptedException {
    // Given
    InputStreamResource resource = new InputStreamResource(InputStream.nullInputStream());
    when(fileExportService.streamGzipFile(any(String.class))).thenReturn(new StreamedFile(resource, ContentLength.of(42L)));

    // When
    CompletableFuture<ResponseEntity<InputStreamResource>> response = fileStreamingController.streamExportZipXmlFile(
        ExportObjectV2.ERSAD_NETEX,
        ExportTypeV2.ACTUAL
    );

    // Then
    assertThat(response.get().getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.get().getBody()).isEqualTo(resource);
    assertThat(Objects.requireNonNull(response.get().getHeaders().getContentType())).hasToString("application/zip");
    assertThat(response.get().getHeaders().getContentLength()).isEqualTo(42L);
    assertThat(response.get().getHeaders().getFirst("Content-Disposition")).contains(".xml.zip");
  }

  @Test
  void shouldStreamLatestExportZipXmlFile() throws ExecutionException, InterruptedException {
    // Given
    InputStreamResource resource = new InputStreamResource(InputStream.nullInputStream());
    when(fileExportService.streamGzipFile(any(String.class))).thenReturn(new StreamedFile(resource, ContentLength.of(42L)));
    when(fileExportService.getLatestUploadedXmlZipFileName(any(ExportFilePathV2.class))).thenReturn(
        "/test-ersad-netex.xml.zip");

    // When
    CompletableFuture<ResponseEntity<InputStreamResource>> response = fileStreamingController.streamLatestExportZipXmlFile(
        ExportObjectV2.ERSAD_NETEX,
        ExportTypeV2.ACTUAL
    );

    // Then
    assertThat(response.get().getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.get().getBody()).isEqualTo(resource);
    assertThat(Objects.requireNonNull(response.get().getHeaders().getContentType())).hasToString("application/zip");
    assertThat(response.get().getHeaders().getContentLength()).isEqualTo(42L);
    assertThat(response.get().getHeaders().getFirst("Content-Disposition")).contains(".xml.zip");
  }

  @Test
  void shouldRejectXmlEndpointsForExportObjectWithoutXmlSupport() {
    // Given
    // When
    // Then
    assertThrows(NotAllowedExportExtensionExceptionV2.class,
        () -> fileStreamingController.streamExportXmlFile(ExportObjectV2.LINE, ExportTypeV2.FULL));
    assertThrows(NotAllowedExportExtensionExceptionV2.class,
        () -> fileStreamingController.streamLatestExportXmlFile(ExportObjectV2.LINE, ExportTypeV2.FULL));
    assertThrows(NotAllowedExportExtensionExceptionV2.class,
        () -> fileStreamingController.streamExportZipXmlFile(ExportObjectV2.LINE, ExportTypeV2.FULL));
    assertThrows(NotAllowedExportExtensionExceptionV2.class,
        () -> fileStreamingController.streamLatestExportZipXmlFile(ExportObjectV2.LINE, ExportTypeV2.FULL));
  }

}
