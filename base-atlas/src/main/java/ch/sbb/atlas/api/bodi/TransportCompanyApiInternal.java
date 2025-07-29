package ch.sbb.atlas.api.bodi;

import ch.sbb.atlas.api.bodi.enumeration.TransportCompanyStatus;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.configuration.Role;
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

@Tag(name = "[INTERNAL] Transport Companies")
@RequestMapping("internal/transport-companies")
public interface TransportCompanyApiInternal {

  @Secured(Role.SECURED_FOR_ATLAS_ADMIN)
  @PostMapping("loadFromBAV")
  void loadTransportCompaniesFromBav();

  @GetMapping
  @PageableAsQueryParam
  Container<TransportCompanyModel> getTransportCompanies(
      @Parameter(hidden = true) Pageable pageable,
      @Parameter @RequestParam(required = false) List<String> searchCriteria,
      @Parameter @RequestParam(required = false) List<TransportCompanyStatus> statusChoices);

  @GetMapping("bySboid")
  List<TransportCompanyModel> getTransportCompaniesBySboid(@Parameter @RequestParam String sboid);

  @GetMapping("{id}")
  TransportCompanyModel getTransportCompany(@PathVariable Long id);

}
