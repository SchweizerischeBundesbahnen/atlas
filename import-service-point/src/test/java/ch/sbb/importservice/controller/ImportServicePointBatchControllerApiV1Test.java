package ch.sbb.importservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.imports.servicepoint.model.ServicePointItemImportResult;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.importservice.ServicePointTestData;
import ch.sbb.importservice.client.SePoDiClient;
import ch.sbb.importservice.service.CsvService;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.MailProducerService;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

public class ImportServicePointBatchControllerApiV1Test extends BaseControllerApiTest {

  @MockBean
  private SePoDiClient sePoDiClient;

  @MockBean
  private CsvService csvService;

  @MockBean
  private MailProducerService mailProducerService;

  @MockBean
  private FileHelperService fileHelperService;

  @Test
  public void shouldPostServicePointImportBatchSuccessfully() throws Exception {
    //given
    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = ServicePointTestData.getServicePointCsvModelContainers();

    List<ServicePointItemImportResult> servicePointItemImportResults = ServicePointTestData.getServicePointItemImportResults(
        servicePointCsvModelContainers);
    when(sePoDiClient.postServicePointsImport(any())).thenReturn(servicePointItemImportResults);

    when(csvService.getActualServicePointCsvModelsFromS3()).thenReturn(servicePointCsvModelContainers);
    doNothing().when(mailProducerService).produceMailNotification(any());

    //when & then
    mvc.perform(post("/v1/import/service-point-batch")
            .contentType(contentType))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldPostServicePointImportBatchWithFileParameterSuccessfully() throws Exception {
    //given
    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = ServicePointTestData.getServicePointCsvModelContainers();
    when(csvService.getActualServicePointCsvModelsFromS3()).thenReturn(servicePointCsvModelContainers);
    doNothing().when(mailProducerService).produceMailNotification(any());
    File file = Files.createTempFile("dir", "file.csv").toFile();
    when(fileHelperService.getFileFromMultipart(any())).thenReturn(file);

    //when & then
    mvc.perform(multipart("/v1/import/service-point").file("file", "example".getBytes(StandardCharsets.UTF_8))
            .contentType(contentType))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldPostServicePointImportBatchWithFileParameterWhenFileIsNotProvided() throws Exception {
    //given
    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = ServicePointTestData.getServicePointCsvModelContainers();
    when(csvService.getActualServicePointCsvModelsFromS3()).thenReturn(servicePointCsvModelContainers);
    doNothing().when(mailProducerService).produceMailNotification(any());

    //when & then
    mvc.perform(multipart("/v1/import/service-point")
            .contentType(contentType))
        .andExpect(status().isBadRequest());
  }

}