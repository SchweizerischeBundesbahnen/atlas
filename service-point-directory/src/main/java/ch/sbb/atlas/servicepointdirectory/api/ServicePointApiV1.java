package ch.sbb.atlas.servicepointdirectory.api;

import static ch.sbb.atlas.model.ResponseCodeDescription.ENTITY_ALREADY_UPDATED;
import static ch.sbb.atlas.model.ResponseCodeDescription.NO_ENTITIES_WERE_MODIFIED;
import static ch.sbb.atlas.model.ResponseCodeDescription.VERSIONING_NOT_IMPLEMENTED;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointFotCommentModel;
import ch.sbb.atlas.api.servicepoint.ServicePointSwissWithGeoModel;
import ch.sbb.atlas.api.servicepoint.UpdateDesignationOfficialServicePointModel;
import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.configuration.Role;
import ch.sbb.atlas.geoupdate.job.model.GeoUpdateItemResultModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointRequestParams;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointSearchRequest;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointSearchResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Service Points")
@RequestMapping("v1/service-points")
public interface ServicePointApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<ReadServicePointVersionModel> getServicePoints(@Parameter(hidden = true) @PageableDefault(sort =
      {ServicePointVersion.Fields.number,
          ServicePointVersion.Fields.validFrom}) Pageable pageable,
      @Valid @ParameterObject ServicePointRequestParams servicePointRequestParams);

  @PostMapping("search")
  List<ServicePointSearchResult> searchServicePoints(@RequestBody @Valid ServicePointSearchRequest value);

  @PostMapping("search-sp-with-route-network")
  List<ServicePointSearchResult> searchServicePointsWithRouteNetworkTrue(@RequestBody @Valid ServicePointSearchRequest value);

  @PostMapping("search-swiss-only")
  List<ServicePointSearchResult> searchSwissOnlyServicePoints(@RequestBody @Valid ServicePointSearchRequest value);

  @GetMapping("{servicePointNumber}")
  List<ReadServicePointVersionModel> getServicePointVersions(@PathVariable Integer servicePointNumber);
  @GetMapping("sloid/{sloid}")
  List<ReadServicePointVersionModel> getServicePointVersionsBySloid(@PathVariable String sloid);

  @GetMapping("versions/{id}")
  ReadServicePointVersionModel getServicePointVersion(@PathVariable Long id);

  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
          + ".ApplicationType).SEPODI)")
  @PostMapping("{servicePointNumber}/revoke")
  List<ReadServicePointVersionModel> revokeServicePoint(@PathVariable Integer servicePointNumber);

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadServicePointVersionModel createServicePoint(@RequestBody @Valid CreateServicePointVersionModel servicePointVersionModel);

  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
          + ".ApplicationType).SEPODI)")
  @ResponseStatus(HttpStatus.OK)
  @PostMapping({"versions/{id}/skip-workflow"})
  ReadServicePointVersionModel validateServicePoint(@PathVariable Long id);

  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "412", description = ENTITY_ALREADY_UPDATED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "501", description = VERSIONING_NOT_IMPLEMENTED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "520", description = NO_ENTITIES_WERE_MODIFIED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  @PutMapping(path = "{id}")
  List<ReadServicePointVersionModel> updateServicePoint(
      @PathVariable Long id,
      @RequestBody @Valid UpdateServicePointVersionModel servicePointVersionModel
  );

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "update-geo/{sloid}/{id}")
  GeoUpdateItemResultModel updateServicePointGeoLocation(@PathVariable String sloid, @PathVariable Long id);

  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).SEPODI)")
  @PutMapping(path = "/update-designation-official/{id}")
  ReadServicePointVersionModel updateDesingationOfficialServicePoint(
      @PathVariable Long id,
      @RequestBody @Valid UpdateDesignationOfficialServicePointModel updateDesignationOfficialServicePointModel
  );

  @PutMapping(path = "/status/{sloid}/{id}")
  ReadServicePointVersionModel updateServicePointStatus(@PathVariable String sloid, @PathVariable Long id,
      @RequestBody @Valid Status status);

  @GetMapping("{servicePointNumber}/fot-comment")
  Optional<ServicePointFotCommentModel> getFotComment(@PathVariable Integer servicePointNumber);

  @PutMapping("{servicePointNumber}/fot-comment")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "412", description = ENTITY_ALREADY_UPDATED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "501", description = VERSIONING_NOT_IMPLEMENTED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "520", description = NO_ENTITIES_WERE_MODIFIED, content =
          @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).SEPODI)")
  ServicePointFotCommentModel saveFotComment(@PathVariable Integer servicePointNumber,
      @Valid @RequestBody ServicePointFotCommentModel fotComment);

  @Secured(Role.SECURED_FOR_ATLAS_ADMIN)
  @PostMapping("/sync-service-points")
  @Operation(description = "Write all Service Points to kafka again for redistribution")
  void syncServicePoints();

  @Secured(Role.SECURED_FOR_ATLAS_ADMIN)
  @GetMapping("/actual-swiss-service-point-with-geo")
  List<ServicePointSwissWithGeoModel> getActualServicePointWithGeolocation();

}
