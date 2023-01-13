package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.base.service.model.api.AtlasApiConstants;
import ch.sbb.atlas.base.service.model.api.Container;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion.Fields;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "LoadingPoints")
@RequestMapping("v1/loading-points")
public interface LoadingPointApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<LoadingPointVersionModel> getLoadingPoints(
      @Parameter(hidden = true) @PageableDefault(sort = {Fields.servicePointNumber, Fields.number, Fields.validFrom}) Pageable pageable,
      @RequestParam(required = false) @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN) Optional<LocalDate> validOn);

  @GetMapping("{servicePointNumber}/{loadingPointNumber}")
  List<LoadingPointVersionModel> getLoadingPoint(@PathVariable Integer servicePointNumber,
      @PathVariable Integer loadingPointNumber);

  @GetMapping("versions/{id}")
  LoadingPointVersionModel getLoadingPointVersion(@PathVariable Long id);

}
