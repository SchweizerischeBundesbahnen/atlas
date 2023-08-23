package ch.sbb.atlas.amazon.service;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.export.enumeration.BoExportFileName;
import ch.sbb.atlas.export.enumeration.ExportType;
import ch.sbb.atlas.export.enumeration.SpExportFileName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class FileServiceImplTest {

  private static final String SEPARATOR = File.separator;

  private FileServiceImpl fileService = new FileServiceImpl();

  @Mock
  private AmazonService amazonService;

  @BeforeEach
  public void setUp() throws IOException {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void shouldCreateZipFile() throws IOException {
    //given
    File dir = new File("./export");
    if (!dir.exists()) {
      dir.mkdirs();
    }
    dir.deleteOnExit();
    Path tempFile = Files.createFile(Paths.get("./export/tmp.csv"));
    tempFile.toFile().deleteOnExit();

    //when
    File zipFile = fileService.zipFile(tempFile.toFile());
    zipFile.deleteOnExit();

    //then
    assertThat(zipFile).isNotNull();
    assertThat(zipFile.getName()).isEqualTo("tmp.csv.zip");
  }

  @Test
  public void shouldGetDirWhenActivatedProfileIsDefined() {
    //given
    fileService.setActiveProfile("dev");
    //when
    String result = fileService.getDir();
    //then
    assertThat(result).isEqualTo("/usr/local/atlas/tmp/");
  }

  @Test
  public void shouldGetDirWhenActivatedProfileIsNull() {
    //given
    fileService.setActiveProfile(null);
    //when
    String result = fileService.getDir();
    //then
    assertThat(result).isEqualTo("." + SEPARATOR + "export" + SEPARATOR);
  }

  @Test
  public void shouldGetDirWhenActivatedProfileIsLocal() {
    //given
    fileService.setActiveProfile("local");
    //when
    String result = fileService.getDir();
    //then
    assertThat(result).isEqualTo("." + SEPARATOR + "export" + SEPARATOR);
  }

  @Test
  void shouldStreamJsonFile() throws IOException {
    String fileName = "full_business_organisation_versions_2023-08-16.json.gz";
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource(fileName).getFile());
    when(amazonService.pullFile(any(), any())).thenReturn(file);
    fileService.setActiveProfile("local");

    StreamingResponseBody result = fileService.streamingJsonFile(ExportType.ACTUAL_DATE, SpExportFileName.TRAFFIC_POINT_ELEMENT_VERSION, amazonService, "fileName", "baseFileName");
    assertThat(result).isNotNull();
  }

  @Test
  void shouldStreamJsonFileAndThrowException() throws IOException {
    String fileName = "fakefile.txt";
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource(fileName).getFile());
    when(amazonService.pullFile(any(), any())).thenReturn(file);
    fileService.setActiveProfile("local");

    assertThrows(FileException.class, () -> fileService.streamingJsonFile(ExportType.ACTUAL_DATE, SpExportFileName.TRAFFIC_POINT_ELEMENT_VERSION, amazonService, "fileName", "baseFileName"));
  }

  @Test
  void shouldStreamGzipFile() throws IOException {
    String fileName = "full_business_organisation_versions_2023-08-16.json.gz";
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource(fileName).getFile());
    when(amazonService.pullFile(any(), any())).thenReturn(file);
    fileService.setActiveProfile("local");

    StreamingResponseBody result = fileService.streamingGzipFile(ExportType.FULL, BoExportFileName.BUSINESS_ORGANISATION_VERSION, amazonService, "fileName", "baseFileName");
    assertThat(result).isNotNull();
  }

}