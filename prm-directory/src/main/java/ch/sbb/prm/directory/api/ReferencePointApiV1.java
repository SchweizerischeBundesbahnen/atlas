package ch.sbb.prm.directory.api;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.prm.model.referencepoint.ReadReferencePointVersionModel;
import ch.sbb.atlas.api.prm.model.referencepoint.ReferencePointVersionModel;
import ch.sbb.prm.directory.controller.model.PrmObjectRequestParams;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Person with Reduced Mobility")
@RequestMapping("v1/reference-points")
public interface ReferencePointApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<ReadReferencePointVersionModel> getReferencePoints(
      @Parameter(hidden = true) @PageableDefault(sort = {BasePrmEntityVersion.Fields.number,
          BasePrmEntityVersion.Fields.validFrom}) Pageable pageable,
      @Valid @ParameterObject PrmObjectRequestParams prmObjectRequestParams);

  @GetMapping("overview/{parentSloid}")
  @PageableAsQueryParam
  Container<ReadReferencePointVersionModel> getReferencePointsOverview(@PathVariable String parentSloid, Pageable pageable);

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadReferencePointVersionModel createReferencePoint(@RequestBody @Valid ReferencePointVersionModel model);

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "{id}")
  List<ReadReferencePointVersionModel> updateReferencePoint(@PathVariable Long id,
      @RequestBody @Valid ReferencePointVersionModel model);

  @GetMapping("{sloid}")
  List<ReadReferencePointVersionModel> getReferencePointVersions(@PathVariable String sloid);

}
