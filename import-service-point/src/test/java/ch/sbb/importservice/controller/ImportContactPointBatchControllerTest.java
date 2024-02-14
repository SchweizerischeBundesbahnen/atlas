package ch.sbb.importservice.controller;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.importservice.client.PrmClient;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.MailProducerService;
import ch.sbb.importservice.service.csv.ContactPointCsvService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ImportContactPointBatchControllerTest extends BaseControllerApiTest {

    @MockBean
    private PrmClient prmClient;

    @MockBean
    private ContactPointCsvService contactPointCsvService;

    @MockBean
    private MailProducerService mailProducerService;

    @MockBean
    private FileHelperService fileHelperService;

    @Test
    void shouldPostContactPointImportBatchSuccessfully() throws Exception {
        //given
        when(prmClient.importContactPoints(any())).thenReturn(List.of());
        when(contactPointCsvService.getActualCsvModelsFromS3()).thenReturn(List.of());
        doNothing().when(mailProducerService).produceMailNotification(any());

        //when & then
        mvc.perform(post("/v1/import-prm/contact-point-batch")
                        .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    void shouldPostInfoDeskImportWithFileParameterSuccessfully() throws Exception {
        //given
        doNothing().when(mailProducerService).produceMailNotification(any());

        File file = Files.createTempFile("dir", "file.csv").toFile();
        when(fileHelperService.getFileFromMultipart(any())).thenReturn(file);

        //when & then
        mvc.perform(multipart("/v1/import-prm/info-desk").file("file", "example".getBytes(StandardCharsets.UTF_8))
                        .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    void shouldPostTicketCounterImportWithFileParameterSuccessfully() throws Exception {
        //given
        doNothing().when(mailProducerService).produceMailNotification(any());

        File file = Files.createTempFile("dir", "file.csv").toFile();
        when(fileHelperService.getFileFromMultipart(any())).thenReturn(file);

        //when & then
        mvc.perform(multipart("/v1/import-prm/ticket-counter").file("file", "example".getBytes(StandardCharsets.UTF_8))
                        .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestWhenFileIsNotProvidedOnInfoDeskFileImport() throws Exception {
        //given
        doNothing().when(mailProducerService).produceMailNotification(any());

        //when & then
        mvc.perform(multipart("/v1/import-prm/info-desk")
                        .contentType(contentType))
                .andExpect(status().isBadRequest());
    }
    @Test
    void shouldReturnBadRequestWhenFileIsNotProvidedOnTicketCounterFileImport() throws Exception {
        //given
        doNothing().when(mailProducerService).produceMailNotification(any());

        //when & then
        mvc.perform(multipart("/v1/import-prm/ticket-counter")
                        .contentType(contentType))
                .andExpect(status().isBadRequest());
    }
}
