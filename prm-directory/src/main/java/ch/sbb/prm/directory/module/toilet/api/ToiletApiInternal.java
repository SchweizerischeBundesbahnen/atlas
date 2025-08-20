package ch.sbb.prm.directory.module.toilet.api;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.prm.model.toilet.ToiletOverviewModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Person with Reduced Mobility")
@RequestMapping("internal/toilets")
public interface ToiletApiInternal {

  @GetMapping("overview/{parentServicePointSloid}")
  List<ToiletOverviewModel> getToiletOverview(@PathVariable String parentServicePointSloid);

}
