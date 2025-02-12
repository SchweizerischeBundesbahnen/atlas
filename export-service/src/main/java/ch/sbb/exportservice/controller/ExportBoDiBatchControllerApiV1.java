package ch.sbb.exportservice.controller;

import ch.sbb.exportservice.service.ExportBusinessOrganisationJobService;
import ch.sbb.exportservice.service.ExportTransportCompanyJobService;
import io.micrometer.tracing.annotation.NewSpan;
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

@Tag(name = "Business Organisations - Export")
@RequestMapping("v1/export/bodi")
@RestController
@AllArgsConstructor
@Slf4j
public class ExportBoDiBatchControllerApiV1 {

  private final ExportTransportCompanyJobService exportTransportCompanyJobService;
  private final ExportBusinessOrganisationJobService exportBusinessOrganisationJobService;

  @PostMapping("transport-company-batch")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @NewSpan
  @Async
  public void startExportTransportCompanyBatch() {
    exportTransportCompanyJobService.startExportJobs();
  }

  @PostMapping("business-organisation-batch")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @NewSpan
  @Async
  public void startExportBusinessOrganisationBatch() {
    //    exportBusinessOrganisationJobService.startExportJobs(); // todo
  }

}
