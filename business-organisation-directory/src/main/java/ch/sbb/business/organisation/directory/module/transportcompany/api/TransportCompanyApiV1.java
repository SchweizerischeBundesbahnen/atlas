package ch.sbb.business.organisation.directory.module.transportcompany.api;

import ch.sbb.atlas.annotation.UnauthorizedAllowed;
import ch.sbb.atlas.annotation.UnauthorizedAllowed.FurtherLimitations;
import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.api.bodi.enumeration.TransportCompanyStatus;
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

@Tag(name = "Transport Companies")
@RequestMapping("v1/transport-companies")
public interface TransportCompanyApiV1 {

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @GetMapping
  @PageableAsQueryParam
  Container<TransportCompanyModel> getTransportCompanies(
      @Parameter(hidden = true) Pageable pageable,
      @Parameter @RequestParam(required = false) List<String> searchCriteria,
      @Parameter @RequestParam(required = false) List<TransportCompanyStatus> statusChoices);

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @GetMapping("{id}")
  TransportCompanyModel getTransportCompany(@PathVariable Long id);

}
