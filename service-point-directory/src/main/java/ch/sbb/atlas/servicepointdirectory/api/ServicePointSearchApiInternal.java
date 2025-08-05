package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointSearchRequest;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointSearchResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "[INTERNAL] Service Points")
@RequestMapping("internal/service-points")
public interface ServicePointSearchApiInternal {

  @PostMapping("search")
  List<ServicePointSearchResult> searchServicePoints(@RequestBody @Valid ServicePointSearchRequest value);

  @PostMapping("search-sp-with-route-network")
  List<ServicePointSearchResult> searchServicePointsWithRouteNetworkTrue(@RequestBody @Valid ServicePointSearchRequest value);

  @PostMapping("search-swiss-only")
  List<ServicePointSearchResult> searchSwissOnlyServicePoints(@RequestBody @Valid ServicePointSearchRequest value);

}
