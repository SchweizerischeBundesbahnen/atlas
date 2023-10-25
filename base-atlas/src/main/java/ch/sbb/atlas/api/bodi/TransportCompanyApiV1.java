package ch.sbb.atlas.api.bodi;

import ch.sbb.atlas.api.bodi.enumeration.TransportCompanyStatus;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.configuration.Role;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Transport Companies")
public interface TransportCompanyApiV1 {

  String BASE_PATH = "v1/transport-companies";

  @Secured(Role.SECURED_FOR_ATLAS_ADMIN)
  @PostMapping(BASE_PATH + "/loadFromBAV")
  void loadTransportCompaniesFromBav();

  @GetMapping(BASE_PATH)
  @PageableAsQueryParam
  Container<TransportCompanyModel> getTransportCompanies(
      @Parameter(hidden = true) Pageable pageable,
      @Parameter @RequestParam(required = false) List<String> searchCriteria,
      @Parameter @RequestParam(required = false) List<TransportCompanyStatus> statusChoices);

  @GetMapping(BASE_PATH + "/bySboid")
  List<TransportCompanyModel> getTransportCompaniesBySboid(@Parameter @RequestParam String sboid);

  @GetMapping(BASE_PATH + "/{id}")
  TransportCompanyModel getTransportCompany(@PathVariable Long id);

}
