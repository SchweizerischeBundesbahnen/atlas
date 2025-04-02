package ch.sbb.exportservice.controller;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.BadRequestException;
import io.micrometer.tracing.annotation.NewSpan;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Export")
@RequestMapping("v2/export")
@RequiredArgsConstructor
@RestController
public class ExportControllerApiV2 {

  private final Map<String, Runnable> exportServiceOperations;

  @PostMapping("{businessType}/{batchName}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Export started successfully"),
      @ApiResponse(responseCode = "400", description = "Not supported export", content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  @NewSpan
  public ResponseEntity<Void> startExport(@PathVariable String businessType, @PathVariable String batchName) {
    final String operationKey = businessType + "/" + batchName;
    if (!exportServiceOperations.containsKey(operationKey)) {
      throw new BadRequestException("Not supporting export of " + operationKey);
    }
    exportServiceOperations.get(operationKey).run();
    return ResponseEntity.ok().build();
  }

}
