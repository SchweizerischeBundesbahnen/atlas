package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.servicepointdirectory.api.model.TrafficPointElementVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion.Fields;
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

@Tag(name = "TrafficPointElements")
@RequestMapping("v1/traffic-point-elements")
public interface TrafficPointElementApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<TrafficPointElementVersionModel> getTrafficPointElements(
      @Parameter(hidden = true) @PageableDefault(sort = {Fields.sloid, Fields.validFrom}) Pageable pageable,
      @Parameter @RequestParam(required = false) List<String> searchCriteria,
      @RequestParam(required = false) @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN) Optional<LocalDate> validOn);

  @GetMapping("{sloid}")
  List<TrafficPointElementVersionModel> getTrafficPointElement(@PathVariable String sloid);

  @GetMapping("versions/{id}")
  TrafficPointElementVersionModel getTrafficPointElementVersion(@PathVariable Long id);

}
