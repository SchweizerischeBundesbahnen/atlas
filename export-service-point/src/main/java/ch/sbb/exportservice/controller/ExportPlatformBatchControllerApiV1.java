package ch.sbb.exportservice.controller;

import ch.sbb.exportservice.service.ExportPlatformJobService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
