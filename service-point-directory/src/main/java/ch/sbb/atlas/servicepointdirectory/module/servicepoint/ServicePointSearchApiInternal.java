package ch.sbb.atlas.servicepointdirectory.module.servicepoint;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.search.ServicePointSearchRequest;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.search.ServicePointSearchResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Service Points")
@RequestMapping("internal/service-points")
public interface ServicePointSearchApiInternal {

  @PostMapping("search")
  List<ServicePointSearchResult> searchServicePoints(@RequestBody @Valid ServicePointSearchRequest value);

  @PostMapping("search-sp-with-route-network")
  List<ServicePointSearchResult> searchServicePointsWithRouteNetworkTrue(@RequestBody @Valid ServicePointSearchRequest value);

  @PostMapping("search-swiss-only")
  List<ServicePointSearchResult> searchSwissOnlyServicePoints(@RequestBody @Valid ServicePointSearchRequest value);

}
