package ch.sbb.prm.directory.service;

import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.repository.RelationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class RelationService {

  private final RelationRepository relationRepository;

  public List<RelationVersion> getRelationsBySloid(String sloid) {
   return relationRepository.findAllBySloid(sloid);
  }
  public List<RelationVersion> getRelationsBySloidAndReferenceType(String sloid, ReferencePointElementType referencePointType) {
   return relationRepository.findAllBySloidAndReferencePointElementType(sloid,referencePointType);
  }
  public List<RelationVersion> getRelationsByParentServicePointSloidAndReferenceType(String parentServicePointSloid,
      ReferencePointElementType referencePointType) {
   return relationRepository.findAllByParentServicePointSloidAndReferencePointElementType(parentServicePointSloid,referencePointType);
  }

}
