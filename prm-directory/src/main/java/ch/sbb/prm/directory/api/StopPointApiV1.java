package ch.sbb.prm.directory.api;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.prm.model.stoppoint.ReadStopPointVersionModel;
import ch.sbb.atlas.api.prm.model.stoppoint.StopPointVersionModel;
import ch.sbb.atlas.configuration.Role;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointImportRequestModel;
import ch.sbb.prm.directory.controller.model.StopPointRequestParams;
import ch.sbb.prm.directory.entity.StopPointVersion;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
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

@Tag(name = "Person with Reduced Mobility")
@RequestMapping("v1/stop-points")
public interface StopPointApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<ReadStopPointVersionModel> getStopPoints(
      @Parameter(hidden = true) @PageableDefault(sort = {StopPointVersion.Fields.number,
          StopPointVersion.Fields.validFrom}) Pageable pageable,
      @Valid @ParameterObject StopPointRequestParams stopPointRequestParams);

  @GetMapping("{sloid}")
  List<ReadStopPointVersionModel> getStopPointVersions(@PathVariable String sloid);

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadStopPointVersionModel createStopPoint(@RequestBody @Valid StopPointVersionModel stopPointVersionModel);

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "{id}")
  List<ReadStopPointVersionModel> updateStopPoint(@PathVariable Long id,
      @RequestBody @Valid StopPointVersionModel stopPointVersionModel);

  @Secured(Role.SECURED_FOR_ATLAS_ADMIN)
  @PostMapping("import")
  List<ItemImportResult> importStopPoints(@RequestBody @Valid StopPointImportRequestModel importRequestModel);

}
