package ch.sbb.exportservice.controller;

import ch.sbb.atlas.api.controller.GzipFileDownloadHttpHeader;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.exportservice.exception.NotAllowedExportFileException;
import ch.sbb.exportservice.model.SePoDiBatchExportFileName;
import ch.sbb.exportservice.model.SePoDiExportType;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static ch.sbb.atlas.api.controller.GzipFileDownloadHttpHeader.extractFileNameFromS3ObjectName;

@Tag(name = "Export Service Point Batch")
@RequestMapping("v1/export")
@RestController
@AllArgsConstructor
@Slf4j
public class ServicePointBatchControllerApiV1 {

    private final FileExportService<SePoDiExportType> fileExportService;

    @GetMapping(value = "json/{exportFileName}/{sePoDiExportType}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the today generated file as Stream"),
            @ApiResponse(responseCode = "404", description = "No file found for today date", content = @Content(schema =
            @Schema(implementation = ErrorResponse.class)))
    })
    @NewSpan
    @Async
    public CompletableFuture<ResponseEntity<InputStreamResource>> streamExportJsonFile(
            @PathVariable SePoDiBatchExportFileName exportFileName,
            @PathVariable SePoDiExportType sePoDiExportType) {
        checkInputPath(exportFileName, sePoDiExportType);
        logInfo(exportFileName.getFileName());
        InputStreamResource body = fileExportService.streamJsonFile(sePoDiExportType, exportFileName);
        return CompletableFuture.supplyAsync(() ->
                ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(body));
    }

    @GetMapping(value = "json/latest/{exportFileName}/{sePoDiExportType}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the today generated file as Stream"),
            @ApiResponse(responseCode = "404", description = "No generated files found", content = @Content(schema =
            @Schema(implementation = ErrorResponse.class)))
    })
    @NewSpan
    @Async
    public CompletableFuture<ResponseEntity<InputStreamResource>> streamLatestExportJsonFile(
            @PathVariable SePoDiBatchExportFileName exportFileName,
            @PathVariable SePoDiExportType sePoDiExportType) {
        checkInputPath(exportFileName, sePoDiExportType);
        String fileName = fileExportService.getLatestUploadedFileName(exportFileName, sePoDiExportType);
        logInfo(fileName);
        InputStreamResource body = fileExportService.streamLatestJsonFile(fileName);
        return CompletableFuture.supplyAsync(() ->
                ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(body));
    }

    @GetMapping(value = "download-gzip-json/{exportFileName}/{sePoDiExportType}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the today generated file"),
            @ApiResponse(responseCode = "404", description = "No file found for today date", content = @Content(schema =
            @Schema(implementation = ErrorResponse.class)))
    })
    @NewSpan
    @Async
    public CompletableFuture<ResponseEntity<InputStreamResource>> streamExportGzFile(
            @PathVariable SePoDiBatchExportFileName exportFileName,
            @PathVariable SePoDiExportType sePoDiExportType) throws NotAllowedExportFileException {
        checkInputPath(exportFileName, sePoDiExportType);
        String fileName = fileExportService.getBaseFileName(sePoDiExportType, exportFileName);
        logInfo(fileName);
        HttpHeaders headers = GzipFileDownloadHttpHeader.getHeaders(fileName);
        InputStreamResource body = fileExportService.streamGzipFile(sePoDiExportType, exportFileName);
        return CompletableFuture.supplyAsync(() -> ResponseEntity.ok().headers(headers).body(body));
    }

    @GetMapping(value = "download-gzip-json/latest/{exportFileName}/{sePoDiExportType}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the latest generated file"),
            @ApiResponse(responseCode = "404", description = "No generated files found", content = @Content(schema =
            @Schema(implementation = ErrorResponse.class)))
    })
    @NewSpan
    @Async
    public CompletableFuture<ResponseEntity<InputStreamResource>> streamLatestExportGzFile(
            @PathVariable SePoDiBatchExportFileName exportFileName,
            @PathVariable SePoDiExportType sePoDiExportType) throws NotAllowedExportFileException {
        checkInputPath(exportFileName, sePoDiExportType);
        String fileName = fileExportService.getLatestUploadedFileName(exportFileName, sePoDiExportType);
        logInfo(fileName);
        HttpHeaders headers = GzipFileDownloadHttpHeader.getHeaders(extractFileNameFromS3ObjectName(fileName));
        InputStreamResource body = fileExportService.streamLatestGzipFile(fileName);
        return CompletableFuture.supplyAsync(() -> ResponseEntity.ok().headers(headers).body(body));
    }

    private void checkInputPath(SePoDiBatchExportFileName exportFileName, SePoDiExportType sePoDiExportType) {
        final List<SePoDiBatchExportFileName> worldOnlyTypes = List.of(
                SePoDiBatchExportFileName.TRAFFIC_POINT_ELEMENT_VERSION,
                SePoDiBatchExportFileName.LOADING_POINT_VERSION
        );
        if (worldOnlyTypes.contains(exportFileName) && !SePoDiExportType.getWorldOnly().contains(sePoDiExportType)) {
            throw new NotAllowedExportFileException(exportFileName, sePoDiExportType);
        }
    }

    private void logInfo(String fileName) {
        log.info("Start streaming file " + fileName + "...");
    }

}
