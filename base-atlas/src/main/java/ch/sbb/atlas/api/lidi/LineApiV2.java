package ch.sbb.atlas.api.lidi;

import static ch.sbb.atlas.model.ResponseCodeDescription.ENTITY_ALREADY_UPDATED;
import static ch.sbb.atlas.model.ResponseCodeDescription.NO_ENTITIES_WERE_MODIFIED;
import static ch.sbb.atlas.model.ResponseCodeDescription.VERSIONING_NOT_IMPLEMENTED;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.DateRange;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Lines")
@RequestMapping("v2/lines")
public interface LineApiV2 {

  @GetMapping("versions/{slnid}")
  List<LineVersionModelV2> getLineVersionsV2(@PathVariable String slnid);

  @PostMapping("versions")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201"),
      @ApiResponse(responseCode = "409", description = "Swiss number is not unique in time", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  LineVersionModelV2 createLineVersionV2(@RequestBody @Valid LineVersionModelV2 newVersion);

  @PutMapping({"versions/{id}"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "409", description = "Swiss number is not unique in time", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "412", description = ENTITY_ALREADY_UPDATED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "501", description = VERSIONING_NOT_IMPLEMENTED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "520", description = NO_ENTITIES_WERE_MODIFIED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  List<LineVersionModelV2> updateLineVersion(@PathVariable Long id, @RequestBody @Valid UpdateLineVersionModelV2 newVersion);

  @GetMapping("/affectedSublines/{id}")
  @Operation(description = "Returns checked Sublines to short")
  AffectedSublines checkAffectedSublines(@PathVariable Long id, @RequestBody @Valid DateRange dateRange);

}
