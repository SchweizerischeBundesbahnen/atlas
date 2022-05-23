package ch.sbb.business.organisation.directory.api;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Business Organisations")
@RequestMapping("v1/business-organisations")
public interface BusinessOrganisationApiV1 {

  @GetMapping
  List<BusinessOrganisationVersionModel> getBusinessOrganisations();

  @PostMapping({"versions"})
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201"),
  })
  BusinessOrganisationVersionModel createBusinessOrganisationVersion(
      @RequestBody @Valid BusinessOrganisationVersionModel newVersion);


  @PostMapping({"versions/{id}"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  List<BusinessOrganisationVersionModel> updateBusinessOrganisationVersion(
      @PathVariable Long id,
      @RequestBody @Valid BusinessOrganisationVersionModel newVersion);
}
