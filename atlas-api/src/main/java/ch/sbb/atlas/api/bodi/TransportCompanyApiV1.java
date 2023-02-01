package ch.sbb.atlas.api.bodi;

import ch.sbb.atlas.api.bodi.enumeration.TransportCompanyStatus;
import ch.sbb.atlas.base.service.model.api.Container;
import ch.sbb.atlas.base.service.model.configuration.Role;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Transport Companies")
@RequestMapping("v1/transport-companies")
public interface TransportCompanyApiV1 {

  @Secured(Role.ROLE_PREFIX + Role.ATLAS_ADMIN)
  @PostMapping("loadFromBAV")
  void loadTransportCompaniesFromBav();

  @GetMapping
  @PageableAsQueryParam
  Container<TransportCompanyModel> getTransportCompanies(
      @Parameter(hidden = true) Pageable pageable,
      @Parameter @RequestParam(required = false) List<String> searchCriteria,
      @Parameter @RequestParam(required = false) List<TransportCompanyStatus> statusChoices);

  @GetMapping("{id}")
  TransportCompanyModel getTransportCompany(@PathVariable Long id);

}
