package ch.sbb.importservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.importservice.client.PrmClient;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.MailProducerService;
import ch.sbb.importservice.service.csv.StopPointCsvService;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

class ImportStopPointBatchControllerTest extends BaseControllerApiTest {

  @MockBean
  private PrmClient sePoDiClient;

  @MockBean
  private StopPointCsvService stopPointCsvService;

  @MockBean
  private MailProducerService mailProducerService;

  @MockBean
  private FileHelperService fileHelperService;


  @Test
  void shouldPostStopPointImportBatchSuccessfully() throws Exception {
    //given
    when(sePoDiClient.postStopPointImport(any())).thenReturn(List.of());
    when(stopPointCsvService.getActualCsvModelsFromS3()).thenReturn(List.of());
    doNothing().when(mailProducerService).produceMailNotification(any());

    //when & then
    mvc.perform(post("/v1/import-prm/stop-point-batch")
            .contentType(contentType))
        .andExpect(status().isOk());
  }

  @Test
  void shouldPostStopPointImportWithFileParameterSuccessfully() throws Exception {
    //given
    doNothing().when(mailProducerService).produceMailNotification(any());

    File file = Files.createTempFile("dir", "file.csv").toFile();
    when(fileHelperService.getFileFromMultipart(any())).thenReturn(file);

    //when & then
    mvc.perform(multipart("/v1/import-prm/stop-point").file("file", "example".getBytes(StandardCharsets.UTF_8))
            .contentType(contentType))
        .andExpect(status().isOk());
  }

  @Test
  void shouldReturnBadRequestWhenFileIsNotProvidedOnStopPointFileImport() throws Exception {
    //given
    doNothing().when(mailProducerService).produceMailNotification(any());

    //when & then
    mvc.perform(multipart("/v1/import-prm/stop-point")
            .contentType(contentType))
        .andExpect(status().isBadRequest());
  }

}