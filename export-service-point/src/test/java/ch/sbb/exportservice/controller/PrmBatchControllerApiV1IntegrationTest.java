package ch.sbb.exportservice.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.exportservice.model.PrmBatchExportFileName;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.service.FileExportService;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.test.web.servlet.MvcResult;

public class PrmBatchControllerApiV1IntegrationTest extends BaseControllerApiTest {

    @MockBean
    private FileExportService<PrmExportType> fileExportService;

    @Test
    void shouldGetJsonSuccessfully() throws Exception {
        //given
        try (InputStream inputStream = this.getClass().getResourceAsStream("/stop-point-data.json")) {
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            doReturn(inputStreamResource).when(fileExportService)
                    .streamJsonFile(PrmExportType.FULL, PrmBatchExportFileName.STOP_POINT_VERSION);
            //when & then
            MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/stop-point-version/full")
                            .contentType(contentType))
                    .andExpect(request().asyncStarted())
                    .andReturn();
            mvc.perform(asyncDispatch(mvcResult))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Test
    void shouldGetJsonUnsuccessfully() throws Exception {
        //given
        doThrow(FileException.class).when(fileExportService)
                .streamJsonFile(PrmExportType.FULL, PrmBatchExportFileName.STOP_POINT_VERSION);
        //when & then
        MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/stop-point-version/full")
                        .contentType(contentType)).andExpect(request().asyncStarted())
                .andReturn();
        mvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldDownloadGzipJsonSuccessfully() throws Exception {
        //given
        try (InputStream inputStream = this.getClass().getResourceAsStream("/stop-point-data.json.gz")) {
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            doReturn(inputStreamResource).when(fileExportService)
                    .streamGzipFile(PrmExportType.FULL, PrmBatchExportFileName.STOP_POINT_VERSION);
            doReturn("service-point").when(fileExportService)
                    .getBaseFileName(PrmExportType.FULL, PrmBatchExportFileName.STOP_POINT_VERSION);
            //when & then
            MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/stop-point-version/full")
                            .contentType(contentType)).andExpect(request().asyncStarted())
                    .andReturn();
            mvc.perform(asyncDispatch(mvcResult))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/gzip"));
        }
    }

    @Test
    void shouldDownloadGzipJsonUnsuccessfully() throws Exception {
        //given
        doThrow(FileException.class).when(fileExportService)
                .streamGzipFile(PrmExportType.FULL, PrmBatchExportFileName.STOP_POINT_VERSION);
        //when & then
        MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/stop-point-version/full")
                        .contentType(contentType)).andExpect(request().asyncStarted())
                .andReturn();
        mvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldDownloadLatestGzipJsonSuccessfully() throws Exception {
        //given
        try (InputStream inputStream = this.getClass().getResourceAsStream("/stop-point-data.json.gz")) {
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            doReturn(inputStreamResource).when(fileExportService)
                    .streamGzipFile(PrmExportType.FULL, PrmBatchExportFileName.STOP_POINT_VERSION);
            doReturn("prm/full/full_stop_point-2023-10-27.json.gz").when(fileExportService)
                    .getLatestUploadedFileName(PrmBatchExportFileName.STOP_POINT_VERSION, PrmExportType.FULL);
            //when & then
            MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/latest/stop-point-version/full")
                            .contentType(contentType)).andExpect(request().asyncStarted())
                    .andReturn();
            mvc.perform(asyncDispatch(mvcResult))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/gzip"));
        }
    }

    @Test
    void shouldDownloadLatestJsonSuccessfully() throws Exception {
        //given
        try (InputStream inputStream = this.getClass().getResourceAsStream("/stop-point-data.json.gz")) {
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            doReturn(inputStreamResource).when(fileExportService)
                    .streamGzipFile(PrmExportType.FULL, PrmBatchExportFileName.STOP_POINT_VERSION);
            doReturn("prm/full/full_stop_point-2023-10-27.json.gz").when(fileExportService)
                    .getLatestUploadedFileName(PrmBatchExportFileName.STOP_POINT_VERSION, PrmExportType.FULL);
            //when & then
            MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/latest/stop-point-version/full")
                            .contentType(contentType)).andExpect(request().asyncStarted())
                    .andReturn();
            mvc.perform(asyncDispatch(mvcResult))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"));
        }
    }

    @Test
    void shouldGetPlatformJsonSuccessfully() throws Exception {
        //given
        try (InputStream inputStream = this.getClass().getResourceAsStream("/platform-data.json")) {
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            doReturn(inputStreamResource).when(fileExportService)
                    .streamJsonFile(PrmExportType.FULL, PrmBatchExportFileName.PLATFORM_VERSION);
            //when & then
            MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/platform-version/full")
                            .contentType(contentType))
                    .andExpect(request().asyncStarted())
                    .andReturn();
            mvc.perform(asyncDispatch(mvcResult))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Test
    void shouldDownloadPlatformGzipJsonSuccessfully() throws Exception {
        //given
        try (InputStream inputStream = this.getClass().getResourceAsStream("/platform-data.json.gz")) {
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            doReturn(inputStreamResource).when(fileExportService)
                    .streamGzipFile(PrmExportType.FULL, PrmBatchExportFileName.PLATFORM_VERSION);
            doReturn("platform").when(fileExportService)
                    .getBaseFileName(PrmExportType.FULL, PrmBatchExportFileName.PLATFORM_VERSION);
            //when & then
            MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/platform-version/full")
                            .contentType(contentType)).andExpect(request().asyncStarted())
                    .andReturn();
            mvc.perform(asyncDispatch(mvcResult))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/gzip"));
        }
    }

    @Test
    void shouldDownloadLatestPlatformGzipJsonSuccessfully() throws Exception {
        //given
        try (InputStream inputStream = this.getClass().getResourceAsStream("/platform-data.json.gz")) {
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            doReturn(inputStreamResource).when(fileExportService)
                    .streamGzipFile(PrmExportType.FULL, PrmBatchExportFileName.PLATFORM_VERSION);
            doReturn("prm/full/full_platform-2023-10-27.json.gz").when(fileExportService)
                    .getLatestUploadedFileName(PrmBatchExportFileName.PLATFORM_VERSION, PrmExportType.FULL);
            //when & then
            MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/latest/platform-version/full")
                            .contentType(contentType)).andExpect(request().asyncStarted())
                    .andReturn();
            mvc.perform(asyncDispatch(mvcResult))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/gzip"));
        }
    }

    @Test
    void shouldDownloadLatestPlatformJsonSuccessfully() throws Exception {
        //given
        try (InputStream inputStream = this.getClass().getResourceAsStream("/platform-data.json.gz")) {
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            doReturn(inputStreamResource).when(fileExportService)
                    .streamGzipFile(PrmExportType.FULL, PrmBatchExportFileName.PLATFORM_VERSION);
            doReturn("prm/full/full_platform-2023-10-27.json.gz").when(fileExportService)
                    .getLatestUploadedFileName(PrmBatchExportFileName.PLATFORM_VERSION, PrmExportType.FULL);
            //when & then
            MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/latest/platform-version/full")
                            .contentType(contentType)).andExpect(request().asyncStarted())
                    .andReturn();
            mvc.perform(asyncDispatch(mvcResult))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"));
        }
    }

    @Test
    void shouldGetReferencePointJsonSuccessfully() throws Exception {
        //given
        try (InputStream inputStream = this.getClass().getResourceAsStream("/reference-point-data.json")) {
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            doReturn(inputStreamResource).when(fileExportService)
                    .streamJsonFile(PrmExportType.FULL, PrmBatchExportFileName.REFERENCE_POINT_VERSION);
            //when & then
            MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/reference-point-version/full")
                            .contentType(contentType))
                    .andExpect(request().asyncStarted())
                    .andReturn();
            mvc.perform(asyncDispatch(mvcResult))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Test
    void shouldDownloadReferencePointGzipJsonSuccessfully() throws Exception {
        //given
        try (InputStream inputStream = this.getClass().getResourceAsStream("/reference-point-data.json.gz")) {
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            doReturn(inputStreamResource).when(fileExportService)
                    .streamGzipFile(PrmExportType.FULL, PrmBatchExportFileName.REFERENCE_POINT_VERSION);
            doReturn("reference-point").when(fileExportService)
                    .getBaseFileName(PrmExportType.FULL, PrmBatchExportFileName.REFERENCE_POINT_VERSION);
            //when & then
            MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/reference-point-version/full")
                            .contentType(contentType)).andExpect(request().asyncStarted())
                    .andReturn();
            mvc.perform(asyncDispatch(mvcResult))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/gzip"));
        }
    }

    @Test
    void shouldGetContactPointJsonSuccessfully() throws Exception {
        //given
        try (InputStream inputStream = this.getClass().getResourceAsStream("/contact-point-data.json")) {
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            doReturn(inputStreamResource).when(fileExportService)
                    .streamJsonFile(PrmExportType.FULL, PrmBatchExportFileName.CONTACT_POINT_VERSION);
            //when & then
            MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/contact-point-version/full")
                            .contentType(contentType))
                    .andExpect(request().asyncStarted())
                    .andReturn();
            mvc.perform(asyncDispatch(mvcResult))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Test
    void shouldDownloadContactPointGzipJsonSuccessfully() throws Exception {
        //given
        try (InputStream inputStream = this.getClass().getResourceAsStream("/contact-point-data.json.gz")) {
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            doReturn(inputStreamResource).when(fileExportService)
                    .streamGzipFile(PrmExportType.FULL, PrmBatchExportFileName.CONTACT_POINT_VERSION);
            doReturn("contact-point").when(fileExportService)
                    .getBaseFileName(PrmExportType.FULL, PrmBatchExportFileName.CONTACT_POINT_VERSION);
            //when & then
            MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/contact-point-version/full")
                            .contentType(contentType)).andExpect(request().asyncStarted())
                    .andReturn();
            mvc.perform(asyncDispatch(mvcResult))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/gzip"));
        }
    }

    @Test
    void shouldGetToiletJsonSuccessfully() throws Exception {
        //given
        try (InputStream inputStream = this.getClass().getResourceAsStream("/toilet-data.json")) {
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            doReturn(inputStreamResource).when(fileExportService)
                    .streamJsonFile(PrmExportType.FULL, PrmBatchExportFileName.TOILET_VERSION);
            //when & then
            MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/toilet-version/full")
                            .contentType(contentType))
                    .andExpect(request().asyncStarted())
                    .andReturn();
            mvc.perform(asyncDispatch(mvcResult))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Test
    void shouldDownloadToiletGzipJsonSuccessfully() throws Exception {
        //given
        try (InputStream inputStream = this.getClass().getResourceAsStream("/toilet-data.json.gz")) {
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            doReturn(inputStreamResource).when(fileExportService)
                    .streamGzipFile(PrmExportType.FULL, PrmBatchExportFileName.TOILET_VERSION);
            doReturn("toilet").when(fileExportService)
                    .getBaseFileName(PrmExportType.FULL, PrmBatchExportFileName.TOILET_VERSION);
            //when & then
            MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/toilet-version/full")
                            .contentType(contentType)).andExpect(request().asyncStarted())
                    .andReturn();
            mvc.perform(asyncDispatch(mvcResult))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/gzip"));
        }
    }

}
