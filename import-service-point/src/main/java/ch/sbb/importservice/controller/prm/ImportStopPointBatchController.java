package ch.sbb.importservice.controller.prm;

import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_STOP_POINT_CSV_JOB_NAME;

import ch.sbb.importservice.service.FileHelperService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
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

@Tag(name = "Import Prm Batch")
@RequestMapping("v1/import-prm")
@RestController
@Slf4j
public class ImportStopPointBatchController extends ImportPrmBatchBaseController {

  private final Job importStopPointCsvJob;

  public ImportStopPointBatchController(JobLauncher jobLauncher, FileHelperService fileHelperService,
      @Qualifier(IMPORT_STOP_POINT_CSV_JOB_NAME) Job importStopPointCsvJob) {
    super(jobLauncher, fileHelperService);
    this.importStopPointCsvJob = importStopPointCsvJob;
  }

  @PostMapping("stop-point-batch")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @Async
  public void startStopPointImportBatch() {
    startBatch(importStopPointCsvJob, IMPORT_STOP_POINT_CSV_JOB_NAME);
  }

  @PostMapping("stop-point")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  public ResponseEntity<String> startStopPointImport(@RequestParam("file") MultipartFile multipartFile) throws IOException {
    return startFileImport(importStopPointCsvJob, IMPORT_STOP_POINT_CSV_JOB_NAME, multipartFile);
  }

}
