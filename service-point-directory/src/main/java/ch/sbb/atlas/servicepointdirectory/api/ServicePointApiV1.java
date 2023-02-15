package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.base.service.imports.servicepoint.model.ServicePointImportReqModel;
import ch.sbb.atlas.base.service.imports.servicepoint.model.ServicePointItemImportResult;
import ch.sbb.atlas.base.service.model.api.Container;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion.Fields;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "ServicePoints")
@RequestMapping("v1/service-points")
public interface ServicePointApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<ServicePointVersionModel> getServicePoints(@Parameter(hidden = true) @PageableDefault(sort = {Fields.number,
      Fields.validFrom}) Pageable pageable, @ParameterObject ServicePointRequestParams servicePointRequestParams);

  @GetMapping("{servicePointNumber}")
  List<ServicePointVersionModel> getServicePoint(@PathVariable Integer servicePointNumber);

  @GetMapping("versions/{id}")
  ServicePointVersionModel getServicePointVersion(@PathVariable Long id);

  @PostMapping("import")
  List<ServicePointItemImportResult> importServicePoints(@RequestBody @Valid ServicePointImportReqModel servicePoints);

}
