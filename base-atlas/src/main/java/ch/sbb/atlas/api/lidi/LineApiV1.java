package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.model.Container;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Lines")
@RequestMapping("v1/lines")
public interface LineApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<LineModel> getLines(@Parameter(hidden = true) Pageable pageable,
      @Valid @ParameterObject LineRequestParams lineRequestParams);

  @GetMapping("{slnid}")
  LineModel getLine(@PathVariable String slnid);

  /**
   * @deprecated
   */
  @Deprecated(forRemoval = true, since = "2.328.0")
  @GetMapping("versions/{slnid}")
  List<LineVersionModel> getLineVersions(@PathVariable String slnid);

}
