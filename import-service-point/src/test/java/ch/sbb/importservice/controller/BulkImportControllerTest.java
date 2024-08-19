package ch.sbb.importservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.importservice.client.ServicePointBulkImportClient;
import ch.sbb.importservice.entity.BulkImport;
import ch.sbb.importservice.repository.BulkImportRepository;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

class BulkImportControllerTest extends BaseControllerApiTest {

  @Autowired
  private BulkImportRepository bulkImportRepository;

  @MockBean
  private ServicePointBulkImportClient servicePointBulkImportClient;

  @MockBean
  private AmazonService amazonService;

  private String todaysDirectory;

  @BeforeEach
  void setUp() {
    todaysDirectory = "e123456/" + DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN).format(LocalDate.now())
        + "/SEPODI/SERVICE_POINT/UPDATE";
    when(amazonService.putFile(eq(AmazonBucket.BULK_IMPORT), any(File.class), anyString()))
        .thenAnswer(i -> URI.create("https://atlas-bulk-import-dev-dev.s3.eu-central-1.amazonaws.com/" +
            todaysDirectory + "/" + i.getArgument(1, File.class).getName()).toURL());
  }

  @AfterEach
  void tearDown() {
    bulkImportRepository.deleteAll();
  }

  @Test
  void shouldAcceptGenericBulkImportWithFile() throws Exception {
    File file = new File(this.getClass().getClassLoader().getResource("service-point-update.csv").getFile());
    mvc.perform(multipart("/v1/import/bulk/SEPODI/SERVICE_POINT/UPDATE")
            .file(new MockMultipartFile("file", "service-point-update.csv", "application/csv", Files.readAllBytes(file.toPath()))))
        .andExpect(status().isAccepted());

    verify(amazonService, timeout(100)).putFile(eq(AmazonBucket.BULK_IMPORT), any(File.class), eq(todaysDirectory));
    verify(servicePointBulkImportClient, timeout(1000).atLeastOnce()).bulkImportUpdate(any());

    assertThat(bulkImportRepository.count()).isEqualTo(1);
    BulkImport bulkImport = bulkImportRepository.findAll().getFirst();
    assertThat(bulkImport.getId()).isNotNull();
    assertThat(bulkImport.getImportFileUrl()).isEqualTo(todaysDirectory + "/service-point-update.csv");
  }
}