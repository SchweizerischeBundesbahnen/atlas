package ch.sbb.line.directory.service.export;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.model.exception.ExportException;
import ch.sbb.line.directory.repository.LineVersionRepository;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LineVersionExportServiceTest {

  @Mock
  private LineVersionRepository lineVersionRepository;

  @Mock
  private FileService fileService;

  @Mock
  private AmazonService amazonService;


  private LineVersionExportService lineVersionExportService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    lineVersionExportService = new LineVersionExportService(fileService, amazonService,
        lineVersionRepository);
  }


  @Test
  void shouldThrowExportExceptionWhenPutCsvFile() throws IOException {
    //given
    when(amazonService.putFile(any(), any())).thenThrow(IOException.class);
    //when

    assertThatExceptionOfType(ExportException.class).isThrownBy(
        () -> lineVersionExportService.putCsvFile(any()));
  }

  @Test
  void shouldThrowExportExceptionWhenPutZipFile() throws IOException {
    //given
    when(amazonService.putZipFile(any(), any())).thenThrow(IOException.class);
    //when

    assertThatExceptionOfType(ExportException.class).isThrownBy(
        () -> lineVersionExportService.putZipFile(any()));
  }


}