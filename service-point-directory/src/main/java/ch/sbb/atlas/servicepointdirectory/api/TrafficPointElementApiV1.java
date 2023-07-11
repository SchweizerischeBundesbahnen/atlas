package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.TrafficPointElementVersionModel;
import ch.sbb.atlas.configuration.Role;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointImportRequestModel;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointItemImportResult;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion.Fields;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Tag(name = "TrafficPointElements")
@RequestMapping("v1/traffic-point-elements")
public interface TrafficPointElementApiV1 {

  @GetMapping
  @PageableAsQueryParam
  @Operation(description = "INFO: Versions of DiDok3 were merged during migration, so there are now a few versions less here.")
  Container<TrafficPointElementVersionModel> getTrafficPointElements(
      @Parameter(hidden = true) @PageableDefault(sort = {Fields.sloid, Fields.validFrom}) Pageable pageable,
      @Parameter @RequestParam(required = false) List<String> searchCriteria,
      @RequestParam(required = false) @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN) Optional<LocalDate> validOn);

  @GetMapping("{sloid}")
  List<TrafficPointElementVersionModel> getTrafficPointElement(@PathVariable String sloid);

  @GetMapping("versions/{id}")
  TrafficPointElementVersionModel getTrafficPointElementVersion(@PathVariable Long id);

  @Secured(Role.SECURED_FOR_ATLAS_ADMIN)
  @PostMapping("import")
  List<TrafficPointItemImportResult> importTrafficPoints(
      @RequestBody @Valid TrafficPointImportRequestModel trafficPointImportRequestModel);

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping()
  TrafficPointElementVersionModel createTrafficPoint(@RequestBody TrafficPointElementVersionModel trafficPointElementVersionModel);

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "{id}")
  List<TrafficPointElementVersionModel> updateTrafficPoint(
          @PathVariable Long id,
          @RequestBody @Valid TrafficPointElementVersionModel trafficPointElementVersionModel
  );


}
