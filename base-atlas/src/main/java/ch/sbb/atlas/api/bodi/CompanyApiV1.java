package ch.sbb.atlas.api.bodi;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Companies")
@RequestMapping("v1/companies")
public interface CompanyApiV1 {

  @Secured(Role.ROLE_PREFIX + Role.ATLAS_ADMIN)
  @PostMapping("loadFromCRD")
  void loadCompaniesFromCrd();

  @GetMapping
  @PageableAsQueryParam
  Container<CompanyModel> getCompanies(
      @Parameter(hidden = true) Pageable pageable,
      @Parameter @RequestParam(required = false) List<String> searchCriteria);

  @GetMapping("{uic}")
  CompanyModel getCompany(@PathVariable Long uic);


}
