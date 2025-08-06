package ch.sbb.atlas.api.bodi;

import ch.sbb.atlas.configuration.Role;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "[INTERNAL] Companies")
@RequestMapping("internal/companies")
public interface CompanyApiInternal {

  @Secured(Role.ROLE_PREFIX + Role.ATLAS_ADMIN)
  @PostMapping("loadFromCRD")
  void loadCompaniesFromCrd();

}