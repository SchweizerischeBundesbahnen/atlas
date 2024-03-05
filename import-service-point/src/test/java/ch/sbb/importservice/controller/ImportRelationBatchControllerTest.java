package ch.sbb.importservice.controller;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.importservice.client.PrmClient;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.MailProducerService;
import ch.sbb.importservice.service.csv.RelationCsvService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ImportRelationBatchControllerTest extends BaseControllerApiTest {

    @MockBean
    private PrmClient prmClient;

    @MockBean
    private RelationCsvService relationCsvService;

    @MockBean
    private MailProducerService mailProducerService;

    @MockBean
    private FileHelperService fileHelperService;

    @Test
    void shouldPostRelationImportBatchSuccessfully() throws Exception {
        //given
        when(prmClient.importContactPoints(any())).thenReturn(List.of());
        when(relationCsvService.getActualCsvModelsFromS3()).thenReturn(List.of());
        doNothing().when(mailProducerService).produceMailNotification(any());

        //when & then
        mvc.perform(post("/v1/import-prm/relation-batch")
                        .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    void shouldPostRelationWithFileParameterSuccessfully() throws Exception {
        //given
        doNothing().when(mailProducerService).produceMailNotification(any());

        File file = Files.createTempFile("dir", "file.csv").toFile();
        when(fileHelperService.getFileFromMultipart(any())).thenReturn(file);

        //when & then
        mvc.perform(multipart("/v1/import-prm/relation").file("file", "example".getBytes(StandardCharsets.UTF_8))
                        .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestWhenFileIsNotProvidedOnRelationFileImport() throws Exception {
        //given
        doNothing().when(mailProducerService).produceMailNotification(any());

        //when & then
        mvc.perform(multipart("/v1/import-prm/relation")
                        .contentType(contentType))
                .andExpect(status().isBadRequest());
    }
}
