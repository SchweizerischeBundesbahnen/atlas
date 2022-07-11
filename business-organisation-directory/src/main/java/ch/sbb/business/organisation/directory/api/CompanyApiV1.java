package ch.sbb.business.organisation.directory.api;

import ch.sbb.business.organisation.directory.configuration.Role;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Companies")
@RequestMapping("v1/companies")
public interface CompanyApiV1 {

  @Secured(Role.ROLE_PREFIX + Role.BO_ADMIN)
  @PostMapping("loadFromCRD")
  void loadCompaniesFromCrd();


}
