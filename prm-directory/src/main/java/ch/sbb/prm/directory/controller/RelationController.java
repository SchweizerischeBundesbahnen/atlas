package ch.sbb.prm.directory.controller;

import ch.sbb.prm.directory.api.RelationApiV1;
import ch.sbb.prm.directory.controller.model.relation.RelationVersionModel;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.mapper.RelationVersionMapper;
import ch.sbb.prm.directory.service.RelationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class RelationController implements RelationApiV1 {

  private final RelationService relationService;

  @Override
  public List<RelationVersionModel> getRelationsBySloid(String sloid) {
    return relationService.getRelationsBySloid(sloid).stream().map(RelationVersionMapper::toModel).sorted().toList();
  }

  @Override
  public List<RelationVersionModel> getRelationsBySloidAndReferenceType(String sloid, ReferencePointElementType referenceType) {
    return relationService.getRelationsBySloidAndReferenceType(sloid, referenceType).stream()
        .map(RelationVersionMapper::toModel).toList();
  }

  @Override
  public List<RelationVersionModel> getRelationsByParentServicePointSloid(String parentServicePointSloid) {
    return relationService.getRelationsByParentServicePointSloid(parentServicePointSloid).stream()
        .map(RelationVersionMapper::toModel).toList();
  }

  @Override
  public List<RelationVersionModel> getRelationsByParentServicePointSloidAndReferenceType(String parentServicePointSloid, ReferencePointElementType referenceType) {
    return relationService.getRelationsByParentServicePointSloidAndReferenceType(parentServicePointSloid, referenceType).stream()
        .map(RelationVersionMapper::toModel).toList();
  }


}
