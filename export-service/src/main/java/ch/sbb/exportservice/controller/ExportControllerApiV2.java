package ch.sbb.exportservice.controller;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.model.ExportObjectV2;
import io.micrometer.tracing.annotation.NewSpan;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Export")
@RequestMapping("v2/export")
@RestController
public class ExportControllerApiV2 {

  private final Map<ExportObjectV2, BaseExportJobService> jobServices;

  ExportControllerApiV2(List<BaseExportJobService> jobServices) {
    this.jobServices = jobServices.stream().collect(Collectors.toMap(BaseExportJobService::getExportObject, Function.identity()));
  }

  @PostMapping({
      "bodi/{exportObject}",
      "prm/{exportObject}",
      "sepodi/{exportObject}",
      "lidi/{exportObject}"
  })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Export started successfully"),
      @ApiResponse(responseCode = "400", description = "Not supported export", content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  @NewSpan
  @Async
  public void startExport(@PathVariable ExportObjectV2 exportObject) {
    jobServices.get(exportObject).startExportJobs();
  }

  @Component
  private static class BatchNameToExportObject implements Converter<String, ExportObjectV2> {

    @Override
    public ExportObjectV2 convert(String source) {
      return switch (source) {
        // bodi
        case "business-organisation-batch" -> ExportObjectV2.BUSINESS_ORGANISATION;
        case "transport-company-batch" -> ExportObjectV2.TRANSPORT_COMPANY;
        // prm
        case "stop-point-batch" -> ExportObjectV2.STOP_POINT;
        case "platform-batch" -> ExportObjectV2.PLATFORM;
        case "reference-point-batch" -> ExportObjectV2.REFERENCE_POINT;
        case "contact-point-batch" -> ExportObjectV2.CONTACT_POINT;
        case "toilet-batch" -> ExportObjectV2.TOILET;
        case "parking-lot-batch" -> ExportObjectV2.PARKING_LOT;
        case "relation-batch" -> ExportObjectV2.RELATION;
        case "recording-obligation-batch" -> ExportObjectV2.RECORDING_OBLIGATION;
        // sepodi
        case "service-point-batch" -> ExportObjectV2.SERVICE_POINT;
        case "traffic-point-batch" -> ExportObjectV2.TRAFFIC_POINT;
        case "loading-point-batch" -> ExportObjectV2.LOADING_POINT;
        // lidi
        case "line-batch" -> ExportObjectV2.LINE;
        case "subline-batch" -> ExportObjectV2.SUBLINE;
        case "ttfn-batch" -> ExportObjectV2.TIMETABLE_FIELD_NUMBER;
        default -> throw new IllegalArgumentException("Invalid export object: " + source);
      };
    }
  }

}
