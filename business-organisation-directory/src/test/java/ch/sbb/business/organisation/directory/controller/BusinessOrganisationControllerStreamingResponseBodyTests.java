package ch.sbb.business.organisation.directory.controller;

import ch.sbb.atlas.export.enumeration.ExportType;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.business.organisation.directory.service.BusinessOrganisationAmazonService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BusinessOrganisationControllerStreamingResponseBodyTests extends BaseControllerApiTest {

    @MockBean
    private BusinessOrganisationAmazonService businessOrganisationAmazonService;

    private StreamingResponseBody writeOutputStream(InputStream inputStream) {
        return outputStream -> {
            int len;
            byte[] data = new byte[4096];
            while ((len = inputStream.read(data, 0, data.length)) != -1) {
                outputStream.write(data, 0, len);
            }
            inputStream.close();
        };
    }

//    @Test
//    void shouldReadJsonAfterExportFullBusinessOrganisationVersions() throws Exception {
//        try (InputStream inputStream = this.getClass().getResourceAsStream("/business-organisation-data.json")) {
//            System.out.println("HERE IS INPUT STRING");
//            System.out.println(inputStream.toString());
//            StreamingResponseBody streamingResponseBody = writeOutputStream(inputStream);
//            Thread.sleep(2000);
//
//            doReturn(streamingResponseBody).when(businessOrganisationAmazonService).streamJsonFile(ExportType.FULL);
//
//            //when & then
//            mvc.perform(get("/v1/business-organisations/export/download-json/" + ExportType.FULL))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$", hasSize(2)));
//        }
//    }
//
//    @Test
//    void shouldReadJsonAfterExportActualBusinessOrganisationVersions() throws Exception {
//        try (InputStream inputStream = this.getClass().getResourceAsStream("/business-organisation-data.json")) {
//            System.out.println("HERE IS INPUT STRING");
//            System.out.println(inputStream.toString());
//            StreamingResponseBody streamingResponseBody = writeOutputStream(inputStream);
//            Thread.sleep(2000);
//
//            doReturn(streamingResponseBody).when(businessOrganisationAmazonService).streamJsonFile(ExportType.ACTUAL_DATE);
//
//            //when & then
//            mvc.perform(get("/v1/business-organisations/export/download-json/" + ExportType.ACTUAL_DATE))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$", hasSize(2)));
//        }
//    }

    @Test
    void shouldReadJsonAfterExportTimetableYearChangeBusinessOrganisationVersions() throws Exception {
        try (InputStream inputStream = this.getClass().getResourceAsStream("/business-organisation-data.json")) {
            StreamingResponseBody streamingResponseBody = writeOutputStream(inputStream);

            doReturn(streamingResponseBody).when(businessOrganisationAmazonService).streamJsonFile(ExportType.FUTURE_TIMETABLE);

            //when & then
            mvc.perform(get("/v1/business-organisations/export/download-json/" + ExportType.FUTURE_TIMETABLE))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)));
        }
    }

}
