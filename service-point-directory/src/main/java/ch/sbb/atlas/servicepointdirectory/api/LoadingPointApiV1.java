package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.servicepoint.CreateLoadingPointVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadLoadingPointVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.service.loadingpoint.LoadingPointRequestParams;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import static ch.sbb.atlas.model.ResponseCodeDescription.*;

@Tag(name = "Loading Points")
@RequestMapping("v1/loading-points")
public interface LoadingPointApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<ReadLoadingPointVersionModel> getLoadingPoints(
      @Parameter(hidden = true) @PageableDefault(sort = {
          LoadingPointVersion.Fields.servicePointNumber,
          LoadingPointVersion.Fields.number, LoadingPointVersion.Fields.validFrom}) Pageable pageable,
      @Valid @ParameterObject LoadingPointRequestParams loadingPointRequestParams);

  @GetMapping("{servicePointNumber}")
  @PageableAsQueryParam
  Container<ReadLoadingPointVersionModel> getLoadingPointOverview(
      @PathVariable Integer servicePointNumber,
      @Parameter(hidden = true) @PageableDefault(sort = {
          LoadingPointVersion.Fields.servicePointNumber,
          LoadingPointVersion.Fields.number, LoadingPointVersion.Fields.validFrom}) Pageable pageable);

  @GetMapping("{servicePointNumber}/{loadingPointNumber}")
  List<ReadLoadingPointVersionModel> getLoadingPoint(@PathVariable Integer servicePointNumber,
      @PathVariable Integer loadingPointNumber);

  @GetMapping("versions/{id}")
  ReadLoadingPointVersionModel getLoadingPointVersion(@PathVariable Long id);

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201"),
      @ApiResponse(responseCode = "409", description = "Number is not unique in time per service point", content =
      @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  ReadLoadingPointVersionModel createLoadingPoint(@RequestBody @Valid CreateLoadingPointVersionModel newVersion);

  @PutMapping({"{id}"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "409", description = "Number is not unique in time per service point", content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "412", description = ENTITY_ALREADY_UPDATED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "501", description = VERSIONING_NOT_IMPLEMENTED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "520", description = NO_ENTITIES_WERE_MODIFIED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  List<ReadLoadingPointVersionModel> updateLoadingPoint(@PathVariable Long id,
      @RequestBody @Valid CreateLoadingPointVersionModel updatedVersion);
}
