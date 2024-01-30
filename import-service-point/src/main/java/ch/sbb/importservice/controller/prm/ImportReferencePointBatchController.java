package ch.sbb.importservice.controller.prm;

import ch.sbb.importservice.service.FileHelperService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_REFERENCE_POINT_CSV_JOB_NAME;

@Tag(name = "Import Prm Batch")
@RequestMapping("v1/import-prm")
@RestController
@Slf4j
public class ImportReferencePointBatchController extends ImportPrmBatchBaseController {

    private final Job importReferencePointCsvJob;

    public ImportReferencePointBatchController(JobLauncher jobLauncher, FileHelperService fileHelperService,
                                               @Qualifier(IMPORT_REFERENCE_POINT_CSV_JOB_NAME) Job importReferencePointCsvJob) {
        super(jobLauncher, fileHelperService);
        this.importReferencePointCsvJob = importReferencePointCsvJob;
    }

    @PostMapping("reference-point-batch")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200")
    })
    @Async
    public void startReferencePointImportBatch() {
        startBatch(importReferencePointCsvJob, IMPORT_REFERENCE_POINT_CSV_JOB_NAME);
    }

    @PostMapping("reference-point")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")
    })
    public ResponseEntity<String> startReferencePointImport(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        return startFileImport(importReferencePointCsvJob, IMPORT_REFERENCE_POINT_CSV_JOB_NAME, multipartFile);
    }

}
