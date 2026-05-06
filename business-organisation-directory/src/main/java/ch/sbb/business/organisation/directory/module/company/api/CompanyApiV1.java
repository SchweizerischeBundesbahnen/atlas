package ch.sbb.business.organisation.directory.module.company.api;

import ch.sbb.atlas.annotation.UnauthorizedAllowed;
import ch.sbb.atlas.annotation.UnauthorizedAllowed.FurtherLimitations;
import ch.sbb.atlas.api.bodi.CompanyModel;
import ch.sbb.atlas.api.model.Container;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Companies")
@RequestMapping("v1/companies")
public interface CompanyApiV1 {

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @GetMapping
  @PageableAsQueryParam
  Container<CompanyModel> getCompanies(
      @Parameter(hidden = true) Pageable pageable,
      @Parameter @RequestParam(required = false) List<String> searchCriteria);

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @GetMapping("{uic}")
  CompanyModel getCompany(@PathVariable String uic);

}