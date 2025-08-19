package ch.sbb.prm.directory.referencepoint.api;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.prm.model.referencepoint.ReadReferencePointVersionModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Person with Reduced Mobility")
@RequestMapping("internal/reference-points")
public interface ReferencePointApiInternal {

  @GetMapping("overview/{parentServicePointSloid}")
  List<ReadReferencePointVersionModel> getReferencePointsOverview(@PathVariable String parentServicePointSloid);

}
