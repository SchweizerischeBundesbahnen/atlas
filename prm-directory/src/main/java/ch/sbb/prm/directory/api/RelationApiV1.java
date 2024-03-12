package ch.sbb.prm.directory.api;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.model.relation.ReadRelationVersionModel;
import ch.sbb.atlas.api.prm.model.relation.RelationVersionModel;
import ch.sbb.atlas.configuration.Role;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.relation.RelationImportRequestModel;
import ch.sbb.prm.directory.controller.model.RelationRequestParams;
import ch.sbb.prm.directory.entity.BasePrmEntityVersion;
import io.swagger.v3.oas.annotations.Parameter;
import ch.sbb.prm.directory.entity.BasePrmEntityVersion.Fields;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
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
@RequestMapping("v1/relations")
public interface RelationApiV1 {

  @GetMapping
  Container<ReadRelationVersionModel> getRelations(
      @Parameter(hidden = true) @PageableDefault(sort = {Fields.sloid,
          BasePrmEntityVersion.Fields.validFrom}) Pageable pageable,
      @Valid @ParameterObject RelationRequestParams prmObjectRequestParams);


  @GetMapping("{sloid}")
  List<ReadRelationVersionModel> getRelationsBySloid(@PathVariable String sloid);

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "{id}")
  List<ReadRelationVersionModel> updateRelation(@PathVariable Long id,
      @RequestBody @Valid RelationVersionModel model);

  @Secured(Role.SECURED_FOR_ATLAS_ADMIN)
  @PostMapping("import")
  List<ItemImportResult> importRelations(@RequestBody @Valid RelationImportRequestModel relationImportRequestModel);
}
