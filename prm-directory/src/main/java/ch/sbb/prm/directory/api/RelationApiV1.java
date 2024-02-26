package ch.sbb.prm.directory.api;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.model.relation.ReadRelationVersionModel;
import ch.sbb.atlas.api.prm.model.relation.RelationVersionModel;
import ch.sbb.prm.directory.controller.model.RelationRequestParams;
import ch.sbb.prm.directory.entity.BasePrmEntityVersion;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Person with Reduced Mobility")
@RequestMapping("v1/relations")
public interface RelationApiV1 {

  @GetMapping
  Container<ReadRelationVersionModel> getRelations(
      @Parameter(hidden = true) @PageableDefault(sort = {BasePrmEntityVersion.Fields.number,
          BasePrmEntityVersion.Fields.validFrom}) Pageable pageable,
      @Valid @ParameterObject RelationRequestParams prmObjectRequestParams);


  @GetMapping("{sloid}")
  List<ReadRelationVersionModel> getRelationsBySloid(@PathVariable String sloid);

  @GetMapping("{sloid}/{referenceType}")
  List<ReadRelationVersionModel> getRelationsBySloidAndReferenceType(@PathVariable String sloid,
      @PathVariable ReferencePointElementType referenceType);

  @GetMapping("parent-service-point-sloid/{parentServicePointSloid}")
  List<ReadRelationVersionModel> getRelationsByParentServicePointSloid(@PathVariable String parentServicePointSloid);

  @GetMapping("parent-service-point-sloid/{parentServicePointSloid}/{referenceType}")
  List<ReadRelationVersionModel> getRelationsByParentServicePointSloidAndReferenceType(
      @PathVariable String parentServicePointSloid,
      @PathVariable ReferencePointElementType referenceType);

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "{id}")
  List<ReadRelationVersionModel> updateRelation(@PathVariable Long id,
      @RequestBody @Valid RelationVersionModel model);
}
