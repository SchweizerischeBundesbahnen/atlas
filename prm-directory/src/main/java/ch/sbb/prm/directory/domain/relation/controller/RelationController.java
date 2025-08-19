package ch.sbb.prm.directory.domain.relation.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.prm.model.relation.ReadRelationVersionModel;
import ch.sbb.atlas.api.prm.model.relation.RelationVersionModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.prm.directory.domain.relation.controller.model.RelationRequestParams;
import ch.sbb.prm.directory.domain.relation.service.RelationService;
import ch.sbb.prm.directory.domain.relation.entity.RelationVersion;
import ch.sbb.prm.directory.domain.relation.mapper.RelationVersionMapper;
import ch.sbb.prm.directory.domain.relation.api.RelationApiV1;
import ch.sbb.prm.directory.domain.relation.search.RelationSearchRestrictions;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class RelationController implements RelationApiV1 {

  private final RelationService relationService;

  @Override
  public Container<ReadRelationVersionModel> getRelations(Pageable pageable, RelationRequestParams relationRequestParams) {
    RelationSearchRestrictions searchRestrictions = RelationSearchRestrictions.builder()
        .pageable(pageable)
        .relationRequestParams(relationRequestParams)
        .build();
    Page<RelationVersion> toiletVersions = relationService.findAll(searchRestrictions);
    return Container.<ReadRelationVersionModel>builder()
        .objects(toiletVersions.stream().map(RelationVersionMapper::toModel).toList())
        .totalCount(toiletVersions.getTotalElements())
        .build();
  }

  @Override
  public List<ReadRelationVersionModel> getRelationsBySloid(String sloid) {
    return relationService.getRelationsBySloid(sloid).stream().map(RelationVersionMapper::toModel).toList();
  }

  @Override
  public List<ReadRelationVersionModel> updateRelation(Long id, RelationVersionModel model) {
    RelationVersion relationVersionToUpdate =
        relationService.getRelationById(id).orElseThrow(() -> new IdNotFoundException(id));
    RelationVersion editedVersion = RelationVersionMapper.toEntity(model);
    relationService.updateRelationVersion(relationVersionToUpdate, editedVersion);

    return relationService.getAllVersions(relationVersionToUpdate.getSloid()).stream()
        .map(RelationVersionMapper::toModel).toList();
  }

}
