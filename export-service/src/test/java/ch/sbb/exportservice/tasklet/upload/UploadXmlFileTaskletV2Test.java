package ch.sbb.exportservice.tasklet.upload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.s3.config.AmazonBucket;
import ch.sbb.atlas.s3.service.AmazonService;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePathV2;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class UploadXmlFileTaskletV2Test {

  @Mock
  private AmazonService amazonService;

  @Test
  void shouldUploadZippedXmlToExportBucket() throws Exception {
    // Given
    ExportFilePathV2 filePath = ExportFilePathV2.getV2Builder(ExportObjectV2.ERSAD_NETEX, ExportTypeV2.ACTUAL)
        .actualDate(LocalDate.of(2025, 1, 1))
        .extension(ExportExtensionFileType.XML_EXTENSION.getExtension())
        .systemDir("target/")
        .build();
    UploadXmlFileTaskletV2 tasklet = new UploadXmlFileTaskletV2(filePath);
    tasklet.amazonService = amazonService;
    when(amazonService.putZipFileCleanupBoth(any(), any(), any())).thenReturn(new URL("https://sbb.ch"));
    ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);
    ArgumentCaptor<String> directoryCaptor = ArgumentCaptor.forClass(String.class);

    // When
    tasklet.putFile();

    // Then
    verify(amazonService).putZipFileCleanupBoth(eq(AmazonBucket.EXPORT), fileCaptor.capture(), directoryCaptor.capture());
    assertThat(fileCaptor.getValue().getPath()).isEqualTo("target/actual-date-ersad-netex-2025-01-01.xml");
    assertThat(directoryCaptor.getValue()).isEqualTo("v2/ersad-netex/actual-date");
  }
}
