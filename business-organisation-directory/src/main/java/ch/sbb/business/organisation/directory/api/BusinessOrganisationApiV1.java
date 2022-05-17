package ch.sbb.business.organisation.directory.api;

import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Business Organisations")
@RequestMapping("v1/business-organisations")
public interface BusinessOrganisationApiV1 {

  @GetMapping
  List<BusinessOrganisationVersion> getBusinessOrganisations();

  @PostMapping({"versions"})
  BusinessOrganisationVersion createBusinessOrganisations();

}
