package ch.sbb.exportservice.controller;

import static ch.sbb.atlas.api.controller.GzipFileDownloadHttpHeader.extractFileNameFromS3ObjectName;

import ch.sbb.atlas.api.controller.GzipFileDownloadHttpHeader;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.exportservice.exception.NotAllowedExportFileException;
import ch.sbb.exportservice.model.PrmBatchExportFileName;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.service.ExportPlatformJobService;
import ch.sbb.exportservice.service.FileExportService;
import io.micrometer.tracing.annotation.NewSpan;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.concurrent.CompletableFuture;

@Tag(name = "Person with Reduced Mobility - Export")
@RequestMapping("v1/export/prm")
@RestController
@AllArgsConstructor
@Slf4j
public class ExportPlatformBatchControllerApiV1 {

    private final ExportPlatformJobService exportPlatformJobService;

    @PostMapping("platform-batch")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200"),
    })
    @Async
    public void startExportServicePointBatch() {
        exportPlatformJobService.startExportJobs();
    }

}
