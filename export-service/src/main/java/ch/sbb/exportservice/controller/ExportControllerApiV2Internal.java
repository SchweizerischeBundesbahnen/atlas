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
public class ExportControllerApiV2Internal {

  private final Map<ExportObjectV2, BaseExportJobService> jobServices;

  ExportControllerApiV2Internal(List<BaseExportJobService> jobServices) {
    this.jobServices = jobServices.stream().collect(Collectors.toMap(BaseExportJobService::getExportObject, Function.identity()));
  }

  @PostMapping({
      "internal/bodi/{exportObject}",
      "internal/prm/{exportObject}",
      "internal/sepodi/{exportObject}",
      "internal/lidi/{exportObject}"
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
      return ExportObjectV2.getExportTypeForBatchServiceName(source);
    }
  }

}
