package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.configuration.Role;
import ch.sbb.atlas.imports.servicepoint.model.ServicePointImportReqModel;
import ch.sbb.atlas.imports.servicepoint.model.ServicePointItemImportResult;
import ch.sbb.atlas.servicepointdirectory.api.model.CreateServicePointVersionModel;
import ch.sbb.atlas.servicepointdirectory.api.model.ReadServicePointVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion.Fields;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "ServicePoints")
@RequestMapping("v1/service-points")
public interface ServicePointApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<ReadServicePointVersionModel> getServicePoints(@Parameter(hidden = true) @PageableDefault(sort = {Fields.number,
      Fields.validFrom}) Pageable pageable, @ParameterObject ServicePointRequestParams servicePointRequestParams);

  @GetMapping("{servicePointNumber}")
  List<ReadServicePointVersionModel> getServicePointVersions(@PathVariable Integer servicePointNumber);

  @GetMapping("versions/{id}")
  ReadServicePointVersionModel getServicePointVersion(@PathVariable Long id);

  @Secured(Role.ROLE_PREFIX + Role.ATLAS_ADMIN)
  @PostMapping("import")
  List<ServicePointItemImportResult> importServicePoints(@RequestBody @Valid ServicePointImportReqModel servicePoints);

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping()
  ReadServicePointVersionModel createServicePoint(@RequestBody @Valid CreateServicePointVersionModel servicePointVersionModel);

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "{id}")
  List<ReadServicePointVersionModel> updateServicePoint(
      @PathVariable Long id,
      @RequestBody @Valid CreateServicePointVersionModel servicePointVersionModel
  );

}
