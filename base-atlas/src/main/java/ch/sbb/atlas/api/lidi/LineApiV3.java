package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.annotation.UnauthorizedAllowed;
import ch.sbb.atlas.annotation.UnauthorizedAllowed.FurtherLimitations;
import ch.sbb.atlas.api.model.Container;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Lines V3")
@RequestMapping("v3/lines")
@Validated
public interface LineApiV3 {

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @GetMapping("versions")
  @PageableAsQueryParam
  @Operation(operationId = "getLineVersionsV3Paged")
  Container<LineVersionModelV3> getLineVersions(@Parameter(hidden = true) Pageable pageable,
      @Valid @ParameterObject LineVersionV3RequestParams params);

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @GetMapping("versions/{slnid}")
  List<LineVersionModelV3> getLineVersionsV3(@PathVariable String slnid);

}
