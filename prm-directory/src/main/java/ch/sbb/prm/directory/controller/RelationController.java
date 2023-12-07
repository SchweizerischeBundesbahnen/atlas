package ch.sbb.prm.directory.controller;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.model.relation.CreateRelationVersionModel;
import ch.sbb.atlas.api.prm.model.relation.ReadRelationVersionModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.prm.directory.api.RelationApiV1;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.mapper.RelationVersionMapper;
import ch.sbb.prm.directory.service.RelationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class RelationController implements RelationApiV1 {

  private final RelationService relationService;

  @Override
  public List<ReadRelationVersionModel> getRelationsBySloid(String sloid) {
    return relationService.getRelationsBySloid(sloid).stream().map(RelationVersionMapper::toModel).toList();
  }

  @Override
  public List<ReadRelationVersionModel> getRelationsBySloidAndReferenceType(String sloid, ReferencePointElementType referenceType) {
    return relationService.getRelationsBySloidAndReferenceType(sloid, referenceType).stream()
        .map(RelationVersionMapper::toModel).toList();
  }

  @Override
  public List<ReadRelationVersionModel> getRelationsByParentServicePointSloid(String parentServicePointSloid) {
    return relationService.getRelationsByParentServicePointSloid(parentServicePointSloid).stream()
        .map(RelationVersionMapper::toModel).toList();
  }

  @Override
  public List<ReadRelationVersionModel> getRelationsByParentServicePointSloidAndReferenceType(String parentServicePointSloid, ReferencePointElementType referenceType) {
    return relationService.getRelationsByParentServicePointSloidAndReferenceType(parentServicePointSloid, referenceType).stream()
        .map(RelationVersionMapper::toModel).toList();
  }

  @Override
  public List<ReadRelationVersionModel> updateRelation(Long id, CreateRelationVersionModel model) {
    RelationVersion relationVersionToUpdate =
        relationService.getRelationById(id).orElseThrow(() -> new IdNotFoundException(id));
    RelationVersion editedVersion = RelationVersionMapper.toEntity(model);
    relationService.updateRelationVersion(relationVersionToUpdate, editedVersion);

    return relationService.getAllVersions(relationVersionToUpdate.getSloid()).stream()
        .map(RelationVersionMapper::toModel).toList();
  }

}
