package ch.sbb.importservice.controller.prm;

import ch.sbb.atlas.api.prm.enumeration.ContactPointType;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static ch.sbb.importservice.utils.JobDescriptionConstants.*;


@Tag(name = "Import Prm Batch")
@RequestMapping("v1/import-prm")
@RestController
@Slf4j
public class ImportContactPointBatchController extends ImportPrmBatchBaseController{
    private final Job importInfoDeskCsvJob;
    private final Job importTicketCounterCsvJob;


    public ImportContactPointBatchController(JobLauncher jobLauncher, FileHelperService fileHelperService,
                                        @Qualifier(IMPORT_INFO_DESK_CSV_JOB_NAME) Job importInfoDeskCsvJob,
                                        @Qualifier(IMPORT_TICKET_COUNTER_CSV_JOB_NAME) Job importTicketCiunterCsvJob) {
        super(jobLauncher, fileHelperService);
        this.importInfoDeskCsvJob = importInfoDeskCsvJob;
        this.importTicketCounterCsvJob = importTicketCiunterCsvJob;
    }

    @PostMapping("contact-point-batch")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
    })
    @Async
    public void startContactPointImportBatch() {
        startBatch(importInfoDeskCsvJob, IMPORT_INFO_DESK_CSV_JOB_NAME);
        startBatch(importTicketCounterCsvJob, IMPORT_TICKET_COUNTER_CSV_JOB_NAME);
    }

    @PostMapping("info-desk")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
    })
    public ResponseEntity<String> startInfoDeskImport(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        return startFileImport(importInfoDeskCsvJob, IMPORT_INFO_DESK_CSV_JOB_NAME, multipartFile);
    }

    @PostMapping("ticket-counter")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
    })
    public ResponseEntity<String> startTicketCounterImport(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        return startFileImport(importTicketCounterCsvJob, IMPORT_TICKET_COUNTER_CSV_JOB_NAME, multipartFile);
    }
}
