package ch.sbb.importservice.controller;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.importservice.client.PrmClient;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.MailProducerService;
import ch.sbb.importservice.service.csv.PlatformCsvService;
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

class ImportPlatformBatchControllerTest extends BaseControllerApiTest {

    @MockBean
    private PrmClient prmClient;

    @MockBean
    private PlatformCsvService platformCsvService;

    @MockBean
    private MailProducerService mailProducerService;

    @MockBean
    private FileHelperService fileHelperService;


    @Test
    void shouldPostPlatformImportBatchSuccessfully() throws Exception {
        //given
        when(prmClient.postStopPointImport(any())).thenReturn(List.of());
        when(platformCsvService.getActualCsvModelsFromS3()).thenReturn(List.of());
        doNothing().when(mailProducerService).produceMailNotification(any());

        //when & then
        mvc.perform(post("/v1/import-prm/platform-batch")
                        .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    void shouldPostPlatformImportWithFileParameterSuccessfully() throws Exception {
        //given
        doNothing().when(mailProducerService).produceMailNotification(any());

        File file = Files.createTempFile("dir", "file.csv").toFile();
        when(fileHelperService.getFileFromMultipart(any())).thenReturn(file);

        //when & then
        mvc.perform(multipart("/v1/import-prm/platform").file("file", "example".getBytes(StandardCharsets.UTF_8))
                        .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestWhenFileIsNotProvidedOnPlatformFileImport() throws Exception {
        //given
        doNothing().when(mailProducerService).produceMailNotification(any());

        //when & then
        mvc.perform(multipart("/v1/import-prm/platform")
                        .contentType(contentType))
                .andExpect(status().isBadRequest());
    }

}
