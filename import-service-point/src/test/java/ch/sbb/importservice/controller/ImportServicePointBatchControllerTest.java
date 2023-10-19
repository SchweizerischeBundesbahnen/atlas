package ch.sbb.importservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.imports.servicepoint.ItemImportResult;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.importservice.ServicePointTestData;
import ch.sbb.importservice.client.SePoDiClient;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.MailProducerService;
import ch.sbb.importservice.service.csv.LoadingPointCsvService;
import ch.sbb.importservice.service.csv.ServicePointCsvService;
import ch.sbb.importservice.service.csv.TrafficPointCsvService;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

 class ImportServicePointBatchControllerTest extends BaseControllerApiTest {

  @MockBean
  private SePoDiClient sePoDiClient;

  @MockBean
  private ServicePointCsvService servicePointCsvService;

  @MockBean
  private LoadingPointCsvService loadingPointCsvService;

  @MockBean
  private TrafficPointCsvService trafficPointCsvService;

  @MockBean
  private MailProducerService mailProducerService;

  @MockBean
  private FileHelperService fileHelperService;

  @Test
   void shouldPostServicePointImportBatchSuccessfully() throws Exception {
    //given
    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = ServicePointTestData
        .getServicePointCsvModelContainers();

    List<ItemImportResult> itemImportResults = ServicePointTestData.getServicePointItemImportResults(
        servicePointCsvModelContainers);
    when(sePoDiClient.postServicePointsImport(any())).thenReturn(itemImportResults);

    when(servicePointCsvService.getActualCsvModelsFromS3()).thenReturn(
        ServicePointTestData.getDefaultServicePointCsvModels(85070001));
    doNothing().when(mailProducerService).produceMailNotification(any());

    //when & then
    mvc.perform(post("/v1/import/service-point-batch")
            .contentType(contentType))
        .andExpect(status().isOk());
  }

  @Test
   void shouldPostServicePointImportBatchWithFileParameterSuccessfully() throws Exception {
    //given
    List<ServicePointCsvModel> servicePointCsvModels = ServicePointTestData
        .getDefaultServicePointCsvModels(85070005);
    when(servicePointCsvService.getActualCsvModelsFromS3()).thenReturn(servicePointCsvModels);
    doNothing().when(mailProducerService).produceMailNotification(any());
    File file = Files.createTempFile("dir", "file.csv").toFile();
    when(fileHelperService.getFileFromMultipart(any())).thenReturn(file);

    //when & then
    mvc.perform(multipart("/v1/import/service-point").file("file", "example".getBytes(StandardCharsets.UTF_8))
            .contentType(contentType))
        .andExpect(status().isOk());
  }

  @Test
   void shouldReturnBadRequestWhenFileIsNotProvidedOnServicePointFileImport() throws Exception {
    //given
    List<ServicePointCsvModel> servicePointCsvModels = ServicePointTestData
        .getDefaultServicePointCsvModels(85070005);
    when(servicePointCsvService.getActualCsvModelsFromS3()).thenReturn(servicePointCsvModels);
    doNothing().when(mailProducerService).produceMailNotification(any());

    //when & then
    mvc.perform(multipart("/v1/import/service-point")
            .contentType(contentType))
        .andExpect(status().isBadRequest());
  }

  @Test
   void shouldPostTrafficPointImportBatchSuccessfully() throws Exception {
    //given
    when(sePoDiClient.postTrafficPointsImport(any())).thenReturn(List.of());
    when(trafficPointCsvService.getActualCsvModelsFromS3()).thenReturn(List.of());
    doNothing().when(mailProducerService).produceMailNotification(any());

    //when & then
    mvc.perform(post("/v1/import/traffic-point-batch")
            .contentType(contentType))
        .andExpect(status().isOk());
  }

  @Test
   void shouldPostTrafficPointImportWithFileParameterSuccessfully() throws Exception {
    //given
    doNothing().when(mailProducerService).produceMailNotification(any());

    File file = Files.createTempFile("dir", "file.csv").toFile();
    when(fileHelperService.getFileFromMultipart(any())).thenReturn(file);

    //when & then
    mvc.perform(multipart("/v1/import/traffic-point").file("file", "example".getBytes(StandardCharsets.UTF_8))
            .contentType(contentType))
        .andExpect(status().isOk());
  }

  @Test
   void shouldReturnBadRequestWhenFileIsNotProvidedOnTrafficPointFileImport() throws Exception {
    //given
    doNothing().when(mailProducerService).produceMailNotification(any());

    //when & then
    mvc.perform(multipart("/v1/import/traffic-point")
            .contentType(contentType))
        .andExpect(status().isBadRequest());
  }

  @Test
   void shouldPostLoadingPointImportBatchSuccessfully() throws Exception {
    //given
    when(sePoDiClient.postLoadingPointsImport(any())).thenReturn(List.of());
    when(loadingPointCsvService.getActualCsvModelsFromS3()).thenReturn(List.of());
    doNothing().when(mailProducerService).produceMailNotification(any());

    //when & then
    mvc.perform(post("/v1/import/loading-point-batch")
            .contentType(contentType))
        .andExpect(status().isOk());
  }

  @Test
   void shouldPostLoadingPointImportWithFileParameterSuccessfully() throws Exception {
    //given
    doNothing().when(mailProducerService).produceMailNotification(any());

    File file = Files.createTempFile("dir", "file.csv").toFile();
    when(fileHelperService.getFileFromMultipart(any())).thenReturn(file);

    //when & then
    mvc.perform(multipart("/v1/import/loading-point").file("file", "example".getBytes(StandardCharsets.UTF_8))
            .contentType(contentType))
        .andExpect(status().isOk());
  }

  @Test
   void shouldReturnBadRequestWhenFileIsNotProvidedOnLoadingPointFileImport() throws Exception {
    //given
    doNothing().when(mailProducerService).produceMailNotification(any());

    //when & then
    mvc.perform(multipart("/v1/import/loading-point")
            .contentType(contentType))
        .andExpect(status().isBadRequest());
  }

 }
