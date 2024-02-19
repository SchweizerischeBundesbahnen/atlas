package ch.sbb.prm.directory.api;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.prm.model.contactpoint.ContactPointOverviewModel;
import ch.sbb.atlas.api.prm.model.contactpoint.ContactPointVersionModel;
import ch.sbb.atlas.api.prm.model.contactpoint.ReadContactPointVersionModel;
import ch.sbb.atlas.configuration.Role;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointImportRequestModel;
import ch.sbb.prm.directory.controller.model.ContactPointObjectRequestParams;
import ch.sbb.prm.directory.entity.BasePrmEntityVersion;
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

@Tag(name = "Person with Reduced Mobility")
@RequestMapping("v1/contact-points")
public interface ContactPointApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<ReadContactPointVersionModel> getContactPoints(
          @Parameter(hidden = true) @PageableDefault(sort = {BasePrmEntityVersion.Fields.number,
                  BasePrmEntityVersion.Fields.validFrom}) Pageable pageable,
          @Valid @ParameterObject ContactPointObjectRequestParams contactPointObjectRequestParams);

  @GetMapping("overview/{parentServicePointSloid}")
  @PageableAsQueryParam
  Container<ContactPointOverviewModel> getContactPointOverview(@Parameter(hidden = true) Pageable pageable,
      @PathVariable String parentServicePointSloid);

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadContactPointVersionModel createContactPoint(@RequestBody @Valid ContactPointVersionModel model);

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "{id}")
  List<ReadContactPointVersionModel> updateContactPoint(@PathVariable Long id,
      @RequestBody @Valid ContactPointVersionModel model);

  @GetMapping("{sloid}")
  List<ReadContactPointVersionModel> getContactPointVersions(@PathVariable String sloid);


  @Secured(Role.SECURED_FOR_ATLAS_ADMIN)
  @PostMapping("import")
  List<ItemImportResult> importContactPoints(@RequestBody @Valid ContactPointImportRequestModel contactPointImportRequestModel);
}
