package ch.sbb.prm.directory.api;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.prm.model.contactpoint.ContactPointOverviewModel;
import ch.sbb.atlas.api.prm.model.contactpoint.ContactPointVersionModel;
import ch.sbb.atlas.api.prm.model.contactpoint.ReadContactPointVersionModel;
import ch.sbb.prm.directory.controller.model.ContactPointObjectRequestParams;
import ch.sbb.prm.directory.entity.BasePrmEntityVersion;
import ch.sbb.prm.directory.entity.BasePrmEntityVersion.Fields;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import static ch.sbb.atlas.model.ResponseCodeDescription.*;

@Tag(name = "Person with Reduced Mobility")
@RequestMapping("v1/contact-points")
public interface ContactPointApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<ReadContactPointVersionModel> getContactPoints(
          @Parameter(hidden = true) @PageableDefault(sort = {Fields.sloid,
                  BasePrmEntityVersion.Fields.validFrom}) Pageable pageable,
          @Valid @ParameterObject ContactPointObjectRequestParams contactPointObjectRequestParams);

  @GetMapping("overview/{parentServicePointSloid}")
  List<ContactPointOverviewModel> getContactPointOverview(@PathVariable String parentServicePointSloid);

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadContactPointVersionModel createContactPoint(@RequestBody @Valid ContactPointVersionModel model);

  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
          @ApiResponse(responseCode = "412", description = ENTITY_ALREADY_UPDATED, content =
          @Content(schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "501", description = VERSIONING_NOT_IMPLEMENTED, content =
          @Content(schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "520", description = NO_ENTITIES_WERE_MODIFIED, content =
          @Content(schema = @Schema(implementation = Exception.class))),
  })
  @PutMapping(path = "{id}")
  List<ReadContactPointVersionModel> updateContactPoint(@PathVariable Long id,
      @RequestBody @Valid ContactPointVersionModel model);

  @GetMapping("{sloid}")
  List<ReadContactPointVersionModel> getContactPointVersions(@PathVariable String sloid);

}
