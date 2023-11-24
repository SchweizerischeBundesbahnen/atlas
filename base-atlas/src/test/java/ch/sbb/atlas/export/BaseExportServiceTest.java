package ch.sbb.atlas.export;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.model.BaseVersionModel;
import ch.sbb.atlas.export.enumeration.ExportType;
import ch.sbb.atlas.export.model.VersionCsvModel;
import ch.sbb.atlas.model.entity.BaseVersion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BaseExportServiceTest {

  private static final URL URL;

  static {
    try {
      URL = new URL("http://example.com");
    } catch (MalformedURLException e) {
      throw new IllegalStateException(e);
    }
  }

  private static final File FILE = new File("demo.txt");

  @Mock
  private FileService fileService;
  @Mock
  private AmazonService amazonService;

  private BaseExportService<DummyVersion> baseExportService;

  @BeforeEach
  void setUp() throws IOException {
    MockitoAnnotations.openMocks(this);
    when(amazonService.putFile(any(), any(), any())).thenReturn(URL);
    when(amazonService.putGzipFile(any(), any(), any())).thenReturn(URL);
    baseExportService = new DummyExportService(fileService, amazonService);
  }

  @Test
  void shouldExportFullVersions() {
    List<URL> urls = baseExportService.exportFullVersions();
    assertEquals(1, urls.size());
  }

  @Test
  void shouldRequireImplementationForFullAllFormats() {
    assertThrows(UnsupportedOperationException.class, () -> baseExportService.exportFullVersionsAllFormats());
  }

  @Test
  void shouldExportActualVersions() {
    List<URL> urls = baseExportService.exportActualVersions();
    assertEquals(1, urls.size());
  }

  @Test
  void shouldRequireImplementationForActualAllFormats() {
    assertThrows(UnsupportedOperationException.class, () -> baseExportService.exportActualVersionsAllFormats());
  }

  @Test
  void shouldExportFutureTimetableVersions() {
    List<URL> urls = baseExportService.exportFutureTimetableVersions();
    assertEquals(1, urls.size());
  }

  @Test
  void shouldRequireImplementationForFutureTimetableVersionsAllFormats() {
    assertThrows(UnsupportedOperationException.class, () -> baseExportService.exportFutureTimetableVersionsAllFormats());
  }

  @Test
  void shouldCreateJsonFile() throws IOException {
    List<DummyVersion> baseVersionModelList = new ArrayList<>();

    File jsonFileResult = baseExportService.createJsonFile(baseVersionModelList, ExportType.FULL);
    assertNotNull(jsonFileResult);
    Files.delete(jsonFileResult.toPath());
  }

  @Test
  void shouldCreateCsvFile() throws IOException {
    List<DummyVersion> versionCsvModels = new ArrayList<>();

    File csvFileResult = baseExportService.createCsvFile(versionCsvModels, ExportType.FULL);
    assertNotNull(csvFileResult);
    Files.delete(csvFileResult.toPath());
  }

  private static class DummyExportService extends BaseExportService<DummyVersion> {

    public DummyExportService(FileService fileService, AmazonService amazonService) {
      super(fileService, amazonService);
    }

    @Override
    protected ObjectWriter getObjectWriter() {
      return new ObjectMapper().writer();
    }

    @Override
    protected String getDirectory() {
      return null;
    }

    @Override
    protected File getFullVersionsCsv() {
      return FILE;
    }

    @Override
    protected File getActualVersionsCsv() {
      return FILE;
    }

    @Override
    protected File getFutureTimetableVersionsCsv() {
      return FILE;
    }

    @Override
    protected String getFileName() {
      return null;
    }

    @Override
    protected List<VersionCsvModel> convertToCsvModel(List<DummyVersion> versions) {
      return new ArrayList<>();
    }

    @Override
    protected List<BaseVersionModel> convertToJsonModel(List<DummyVersion> versions) {
      return new ArrayList<>();
    }
  }

  @Data
  @EqualsAndHashCode(callSuper = true)
  private static class DummyVersion extends BaseVersion {

    private LocalDate validFrom;
    private LocalDate validTo;
  }

}
