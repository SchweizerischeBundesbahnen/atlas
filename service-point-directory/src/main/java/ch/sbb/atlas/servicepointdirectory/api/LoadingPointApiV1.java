package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.LoadingPointVersionModel;
import ch.sbb.atlas.configuration.Role;
import ch.sbb.atlas.imports.servicepoint.ItemImportResult;
import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointImportRequestModel;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.service.loadingpoint.LoadingPointElementRequestParams;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "LoadingPoints")
@RequestMapping("v1/loading-points")
public interface LoadingPointApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<LoadingPointVersionModel> getLoadingPoints(
      @Parameter(hidden = true) @PageableDefault(sort = {
          LoadingPointVersion.Fields.servicePointNumber,
          LoadingPointVersion.Fields.number, LoadingPointVersion.Fields.validFrom}) Pageable pageable,
          @ParameterObject LoadingPointElementRequestParams loadingPointElementRequestParams);

  @GetMapping("{servicePointNumber}/{loadingPointNumber}")
  List<LoadingPointVersionModel> getLoadingPoint(@PathVariable Integer servicePointNumber,
      @PathVariable Integer loadingPointNumber);

  @GetMapping("versions/{id}")
  LoadingPointVersionModel getLoadingPointVersion(@PathVariable Long id);

  @Secured(Role.SECURED_FOR_ATLAS_ADMIN)
  @PostMapping("import")
  List<ItemImportResult> importLoadingPoints(
      @RequestBody @Valid LoadingPointImportRequestModel loadingPointImportRequestModel);

}
