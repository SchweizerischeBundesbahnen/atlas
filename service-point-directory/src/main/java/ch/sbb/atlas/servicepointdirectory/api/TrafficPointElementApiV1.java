package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.CreateTrafficPointElementVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadTrafficPointElementVersionModel;
import ch.sbb.atlas.configuration.Role;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointImportRequestModel;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion.Fields;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementRequestParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Traffic Point Elements")
@RequestMapping("v1/traffic-point-elements")
public interface TrafficPointElementApiV1 {

  @GetMapping
  @PageableAsQueryParam
  @Operation(description = "INFO: Versions of DiDok3 were merged during migration, so there are now a few versions less here.")
  Container<ReadTrafficPointElementVersionModel> getTrafficPointElements(
      @Parameter(hidden = true) @PageableDefault(sort = {TrafficPointElementVersion.Fields.sloid,
          Fields.validFrom}, direction = Direction.ASC) Pageable pageable,
      @Valid @ParameterObject TrafficPointElementRequestParams trafficPointElementRequestParams);

  @GetMapping("{sloid}")
  List<ReadTrafficPointElementVersionModel> getTrafficPointElement(@PathVariable String sloid);

  @PageableAsQueryParam
  @GetMapping("/areas/{servicePointNumber}")
  Container<ReadTrafficPointElementVersionModel> getAreasOfServicePoint(@PathVariable Integer servicePointNumber,
      @Parameter(hidden = true) @PageableDefault(sort = {Fields.sloid,
          Fields.validFrom}, direction = Direction.ASC, size = 500) Pageable pageable);

  @PageableAsQueryParam
  @GetMapping("/platforms/{servicePointNumber}")
  Container<ReadTrafficPointElementVersionModel> getPlatformsOfServicePoint(@PathVariable Integer servicePointNumber,
      @Parameter(hidden = true) @PageableDefault(sort = {Fields.sloid,
          Fields.validFrom}, direction = Direction.ASC, size = 500) Pageable pageable);

  @GetMapping("actual-date/{servicePointNumber}")
  List<ReadTrafficPointElementVersionModel> getTrafficPointsOfServicePointValidToday(@PathVariable Integer servicePointNumber);

  @GetMapping("versions/{id}")
  ReadTrafficPointElementVersionModel getTrafficPointElementVersion(@PathVariable Long id);

  @Secured(Role.SECURED_FOR_ATLAS_ADMIN)
  @PostMapping("import")
  List<ItemImportResult> importTrafficPoints(
      @RequestBody @Valid TrafficPointImportRequestModel trafficPointImportRequestModel);

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadTrafficPointElementVersionModel createTrafficPoint(
      @RequestBody @Valid CreateTrafficPointElementVersionModel trafficPointElementVersionModel);

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "{id}")
  List<ReadTrafficPointElementVersionModel> updateTrafficPoint(
      @PathVariable Long id,
      @RequestBody @Valid CreateTrafficPointElementVersionModel trafficPointElementVersionModel
  );

}
