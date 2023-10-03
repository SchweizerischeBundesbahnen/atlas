package ch.sbb.prm.directory.controller;

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

  private final RelationService referencePointService;

  @Override
  public List<RelationVersionModel> getRelationsBySloid(String sloid) {
    return referencePointService.getRelationsBySloid(sloid).stream().map(RelationVersionMapper::toModel).sorted().toList();
  }

  @Override
  public List<RelationVersionModel> getRelationsBySloidAndReferenceType(String sloid, ReferencePointElementType referenceType) {
    return referencePointService.getRelationsBySloidAndReferenceType(sloid, referenceType).stream()
        .map(RelationVersionMapper::toModel).toList();
  }

  @Override
  public List<RelationVersionModel> getRelationsByParentServicePointSloidAndReferenceType(String parentServicePointSloid, ReferencePointElementType referenceType) {
    return referencePointService.getRelationsByParentServicePointSloidAndReferenceType(parentServicePointSloid, referenceType).stream()
        .map(RelationVersionMapper::toModel).toList();
  }


}
