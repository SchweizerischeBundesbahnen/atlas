package ch.sbb.atlas.api.bodi;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.model.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Business Organisations")
@RequestMapping("v1/business-organisations")
public interface BusinessOrganisationApiV1 {

  @Operation(deprecated = true, summary = "Use /v1/business-organisations/versions instead. Will be removed by 01.03.2026.")
  @GetMapping
  @PageableAsQueryParam
  Container<BusinessOrganisationModel> getAllBusinessOrganisations(
      @Parameter(hidden = true) Pageable pageable,
      @Parameter @RequestParam(required = false) List<String> searchCriteria,
      @Parameter @RequestParam(required = false) List<String> inSboids,
      @Parameter @RequestParam(required = false) @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN) LocalDate validOn,
      @Parameter @RequestParam(required = false) List<Status> statusChoices
  );

  @GetMapping("versions")
  @PageableAsQueryParam
  Container<BusinessOrganisationVersionModel> getBusinessOrganisationVersions(
      @Parameter(hidden = true) Pageable pageable,
      @ParameterObject BusinessOrganisationVersionRequestParams businessOrganisationVersionRequestParams);

  @GetMapping("versions/{sboid}")
  List<BusinessOrganisationVersionModel> getVersions(@PathVariable String sboid);
}
