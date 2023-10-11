package ch.sbb.prm.directory.api;

import ch.sbb.prm.directory.controller.model.relation.CreateRelationVersionModel;
import ch.sbb.prm.directory.controller.model.relation.ReadRelationVersionModel;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "PRM - Person with Reduced Mobility")
@RequestMapping("v1/relations")
public interface RelationApiV1 {

  @GetMapping("{sloid}")
  List<ReadRelationVersionModel> getRelationsBySloid(@PathVariable String sloid);

  @GetMapping("{sloid}/{referenceType}")
  List<ReadRelationVersionModel> getRelationsBySloidAndReferenceType(@PathVariable String sloid,
      @PathVariable ReferencePointElementType referenceType);

  @GetMapping("parent-service-point-sloid/{parentServicePointSloid}")
  List<ReadRelationVersionModel> getRelationsByParentServicePointSloid(@PathVariable String parentServicePointSloid);

  @GetMapping("parent-service-point-sloid/{parentServicePointSloid}/{referenceType}")
  List<ReadRelationVersionModel> getRelationsByParentServicePointSloidAndReferenceType(@PathVariable String parentServicePointSloid,
      @PathVariable ReferencePointElementType referenceType);

  @ResponseStatus(HttpStatus.OK)
  @PostMapping(path = "{id}")
  List<ReadRelationVersionModel> updateToiletVersion(@PathVariable Long id,
      @RequestBody @Valid CreateRelationVersionModel model);
}
