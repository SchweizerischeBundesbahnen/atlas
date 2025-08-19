package ch.sbb.prm.directory.contactpoint.api;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.prm.model.contactpoint.ContactPointOverviewModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Person with Reduced Mobility")
@RequestMapping("internal/contact-points")
public interface ContactPointApiInternal {

  @GetMapping("overview/{parentServicePointSloid}")
  List<ContactPointOverviewModel> getContactPointOverview(@PathVariable String parentServicePointSloid);

}
