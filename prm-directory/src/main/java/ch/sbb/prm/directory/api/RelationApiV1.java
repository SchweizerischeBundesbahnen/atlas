package ch.sbb.prm.directory.api;

import ch.sbb.prm.directory.controller.model.RelationVersionModel;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Relation")
@RequestMapping("v1/relations")
public interface RelationApiV1 {

  @GetMapping("{sloid}")
  List<RelationVersionModel> getRelationsBySloid(@PathVariable String sloid);

  @GetMapping("{sloid}/{referenceType}")
  List<RelationVersionModel> getRelationsBySloidAndReferenceType(@PathVariable String sloid,
      @PathVariable ReferencePointElementType referenceType);

  @GetMapping("parent-service-point-sloid/{parentServicePointSloid}/{referenceType}")
  List<RelationVersionModel> getRelationsByParentServicePointSloidAndReferenceType(@PathVariable String parentServicePointSloid,
      @PathVariable ReferencePointElementType referenceType);
}
