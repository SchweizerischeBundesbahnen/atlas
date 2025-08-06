package ch.sbb.business.organisation.directory.controller;

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

  @GetMapping
  @PageableAsQueryParam
  Container<CompanyModel> getCompanies(
      @Parameter(hidden = true) Pageable pageable,
      @Parameter @RequestParam(required = false) List<String> searchCriteria);

  @GetMapping("{uic}")
  CompanyModel getCompany(@PathVariable String uic);

}