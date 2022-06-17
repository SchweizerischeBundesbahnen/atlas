package ch.sbb.business.organisation.directory.api;

import ch.sbb.atlas.model.api.Container;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Transport Companies")
@RequestMapping("v1/transport-companies")
public interface TransportCompanyApiV1 {

  @PostMapping("loadFromBAV")
  void loadTransportCompaniesFromBav();

  @GetMapping
  @PageableAsQueryParam
  Container<TransportCompanyModel> getTransportCompanies(
      @Parameter(hidden = true) Pageable pageable,
      @Parameter @RequestParam(required = false) List<String> searchCriteria);

}
