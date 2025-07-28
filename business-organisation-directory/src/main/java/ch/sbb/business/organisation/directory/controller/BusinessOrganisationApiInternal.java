package ch.sbb.business.organisation.directory.controller;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.bodi.BusinessOrganisationModel;
import ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.configuration.Role;
import ch.sbb.atlas.model.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "[INTERNAL] Business Organisations")
@RequestMapping("internal/business-organisations")
public interface BusinessOrganisationApiInternal {

  @GetMapping
  @PageableAsQueryParam
  Container<BusinessOrganisationModel> getAllBusinessOrganisations(
      @Parameter(hidden = true) Pageable pageable,
      @Parameter @RequestParam(required = false) List<String> searchCriteria,
      @Parameter @RequestParam(required = false) List<String> inSboids,
      @Parameter @RequestParam(required = false) @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN) Optional<LocalDate> validOn,
      @Parameter @RequestParam(required = false) List<Status> statusChoices
  );

  @PostMapping("{sboid}/revoke")
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).BODI)")
  List<BusinessOrganisationVersionModel> revokeBusinessOrganisation(@PathVariable String sboid);

  @PostMapping("versions")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201"),
  })
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).BODI)")
  BusinessOrganisationVersionModel createBusinessOrganisationVersion(
      @RequestBody @Valid BusinessOrganisationVersionModel newVersion);

  // todo: change in frontend and backend to put
  @PutMapping("versions/{id}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).BODI)")
  List<BusinessOrganisationVersionModel> updateBusinessOrganisationVersion(
      @PathVariable Long id,
      @RequestBody @Valid BusinessOrganisationVersionModel newVersion);

  @DeleteMapping("{sboid}")
  void deleteBusinessOrganisation(@PathVariable String sboid);

  @Secured(Role.SECURED_FOR_ATLAS_ADMIN)
  @PostMapping("/sync-business-organisations")
  @Operation(description = "Write all Business Organisations to kafka again for redistribution")
  void syncBusinessOrganisations();
}
