package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.ReadTrafficPointElementVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion.Fields;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Traffic Point Elements")
@RequestMapping("internal/traffic-point-elements")
@Validated
public interface TrafficPointElementApiInternal {

  @PageableAsQueryParam
  @GetMapping("/areas/{servicePointNumber}")
  Container<ReadTrafficPointElementVersionModel> getAreasOfServicePoint(@PathVariable Integer servicePointNumber,
      @Parameter(hidden = true)
      @PageableDefault(sort = {Fields.sloid, Fields.validFrom}, direction = Direction.ASC, size = 500) Pageable pageable);

  @PageableAsQueryParam
  @GetMapping("/platforms/{servicePointNumber}")
  Container<ReadTrafficPointElementVersionModel> getPlatformsOfServicePoint(@PathVariable Integer servicePointNumber,
      @Parameter(hidden = true)
      @PageableDefault(sort = {Fields.sloid, Fields.validFrom}, direction = Direction.ASC, size = 500) Pageable pageable);

  @GetMapping("actual-date/{servicePointNumber}")
  List<ReadTrafficPointElementVersionModel> getTrafficPointsOfServicePointValidToday(@PathVariable Integer servicePointNumber);

}
