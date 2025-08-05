package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.ReadLoadingPointVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Loading Points")
@RequestMapping("internal/loading-points")
public interface LoadingPointApiInternal {

  @GetMapping("{servicePointNumber}")
  @PageableAsQueryParam
  Container<ReadLoadingPointVersionModel> getLoadingPointOverview(
      @PathVariable Integer servicePointNumber,
      @Parameter(hidden = true) @PageableDefault(sort = {
          LoadingPointVersion.Fields.servicePointNumber,
          LoadingPointVersion.Fields.number, LoadingPointVersion.Fields.validFrom}) Pageable pageable);

}
