package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.LoadingPointVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.service.loadingpoint.LoadingPointElementRequestParams;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "LoadingPoints")
@RequestMapping("v1/loading-points")
public interface LoadingPointApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<LoadingPointVersionModel> getLoadingPoints(
          @Parameter(hidden = true) @PageableDefault(sort = {TrafficPointElementVersion.Fields.sloid}) Pageable pageable,
          @ParameterObject LoadingPointElementRequestParams loadingPointElementRequestParams);

  @GetMapping("{servicePointNumber}/{loadingPointNumber}")
  List<LoadingPointVersionModel> getLoadingPoint(@PathVariable Integer servicePointNumber,
      @PathVariable Integer loadingPointNumber);

  @GetMapping("versions/{id}")
  LoadingPointVersionModel getLoadingPointVersion(@PathVariable Long id);

}
