package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointFotCommentModel;
import ch.sbb.atlas.configuration.Role;
import ch.sbb.atlas.imports.servicepoint.ItemImportResult;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointImportRequestModel;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "ServicePoints")
@RequestMapping("v1/service-points")
public interface ServicePointApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<ReadServicePointVersionModel> getServicePoints(@Parameter(hidden = true) @PageableDefault(sort =
      {ServicePointVersion.Fields.number,
          ServicePointVersion.Fields.validFrom}) Pageable pageable,
      @ParameterObject ServicePointRequestParams servicePointRequestParams);
  @PostMapping("search")
  List<ServicePointSearchResult> searchServicePoints(@RequestBody ServicePointSearchRequest value);

  @GetMapping("{servicePointNumber}")
  List<ReadServicePointVersionModel> getServicePointVersions(@PathVariable Integer servicePointNumber);

  @GetMapping("versions/{id}")
  ReadServicePointVersionModel getServicePointVersion(@PathVariable Long id);

  @Secured(Role.SECURED_FOR_ATLAS_ADMIN)
  @PostMapping("import")
  List<ItemImportResult> importServicePoints(@RequestBody @Valid ServicePointImportRequestModel servicePoints);

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadServicePointVersionModel createServicePoint(@RequestBody @Valid CreateServicePointVersionModel servicePointVersionModel);

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "{id}")
  List<ReadServicePointVersionModel> updateServicePoint(
      @PathVariable Long id,
      @RequestBody @Valid CreateServicePointVersionModel servicePointVersionModel
  );

  @GetMapping("{servicePointNumber}/fot-comment")
  Optional<ServicePointFotCommentModel> getFotComment(@PathVariable Integer servicePointNumber);

  @PutMapping("{servicePointNumber}/fot-comment")
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).SEPODI)")
  ServicePointFotCommentModel saveFotComment(@PathVariable Integer servicePointNumber,
      @Valid @RequestBody ServicePointFotCommentModel fotComment);
}
