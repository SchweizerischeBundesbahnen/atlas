package ch.sbb.atlas.api.lidi;

import static ch.sbb.atlas.model.ResponseCodeDescription.ENTITY_ALREADY_UPDATED;
import static ch.sbb.atlas.model.ResponseCodeDescription.NO_ENTITIES_WERE_MODIFIED;
import static ch.sbb.atlas.model.ResponseCodeDescription.VERSIONING_NOT_IMPLEMENTED;

import ch.sbb.atlas.api.model.ErrorResponse;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Timetable Field Numbers")
@RequestMapping("v1/field-numbers")
public interface TimetableFieldNumberApiV1 {

  @GetMapping("/versions/{ttfnId}")
  List<TimetableFieldNumberVersionModel> getAllVersionsVersioned(@PathVariable String ttfnId);

  @PostMapping("/versions/{id}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "409", description = "Number or SwissTimeTableFieldNumber are already taken", content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "412", description = ENTITY_ALREADY_UPDATED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "501", description = VERSIONING_NOT_IMPLEMENTED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "520", description = NO_ENTITIES_WERE_MODIFIED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),

  })
  List<TimetableFieldNumberVersionModel> updateVersionWithVersioning(@PathVariable Long id,
      @RequestBody @Valid TimetableFieldNumberVersionModel newVersion);

  @PostMapping("/versions")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201"),
      @ApiResponse(responseCode = "409", description = "Number or SwissTimeTableFieldNumber are already taken", content =
      @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  TimetableFieldNumberVersionModel createVersion(
      @RequestBody @Valid TimetableFieldNumberVersionModel newVersion);

}
