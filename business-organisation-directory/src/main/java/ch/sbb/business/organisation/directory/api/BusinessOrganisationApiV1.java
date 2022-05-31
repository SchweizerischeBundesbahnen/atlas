package ch.sbb.business.organisation.directory.api;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.api.AtlasApiConstants;
import ch.sbb.atlas.model.api.Container;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Business Organisations")
@RequestMapping("v1/business-organisations")
public interface BusinessOrganisationApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<BusinessOrganisationModel> getAllBusinessOrganisations(
      @Parameter(hidden = true) Pageable pageable,
      @Parameter @RequestParam(required = false) List<String> searchCriteria,
      @Parameter @RequestParam(required = false) @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN) Optional<LocalDate> validOn,
      @Parameter @RequestParam(required = false) List<Status> statusChoices);

  @GetMapping("versions/{sboid}")
  List<BusinessOrganisationVersionModel> getBusinessOrganisationVersions(@PathVariable String sboid);

  @PostMapping("versions")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201"),
  })
  BusinessOrganisationVersionModel createBusinessOrganisationVersion(
      @RequestBody @Valid BusinessOrganisationVersionModel newVersion);


  @PostMapping("versions/{id}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  List<BusinessOrganisationVersionModel> updateBusinessOrganisationVersion(
      @PathVariable Long id,
      @RequestBody @Valid BusinessOrganisationVersionModel newVersion);

  @DeleteMapping("{sboid}")
  void deleteBusinessOrganisation(@PathVariable String sboid);
}
