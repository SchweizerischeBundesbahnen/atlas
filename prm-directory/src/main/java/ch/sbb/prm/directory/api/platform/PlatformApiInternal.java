package ch.sbb.prm.directory.api.platform;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.prm.model.platform.PlatformOverviewModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Person with Reduced Mobility")
@RequestMapping("internal/platforms")
public interface PlatformApiInternal {

  @PageableAsQueryParam
  @GetMapping("/overview/{parentSloid}")
  List<PlatformOverviewModel> getPlatformOverview(@PathVariable String parentSloid);

}
