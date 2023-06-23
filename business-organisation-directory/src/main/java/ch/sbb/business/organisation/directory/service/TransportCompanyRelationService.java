package ch.sbb.business.organisation.directory.service;

import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.business.organisation.directory.entity.TransportCompanyRelation;
import ch.sbb.atlas.model.exception.SboidNotFoundException;
import ch.sbb.business.organisation.directory.exception.TransportCompanyNotFoundException;
import ch.sbb.business.organisation.directory.exception.TransportCompanyRelationConflictException;
import ch.sbb.business.organisation.directory.repository.TransportCompanyRelationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransportCompanyRelationService {

  private final TransportCompanyRelationRepository transportCompanyRelationRepository;

  private final BusinessOrganisationService businessOrganisationService;
  private final TransportCompanyService transportCompanyService;

  public TransportCompanyRelation save(TransportCompanyRelation entity, boolean isUpdateTransportCompanyRelation) {
    if (businessOrganisationService.findBusinessOrganisationVersions(entity.getSboid()).isEmpty()) {
      throw new SboidNotFoundException(entity.getSboid());
    }
    if (!transportCompanyService.existsById(entity.getTransportCompany().getId())) {
      throw new TransportCompanyNotFoundException(entity.getTransportCompany().getId());
    }
    validateRelationOverlaps(entity, isUpdateTransportCompanyRelation);
    return transportCompanyRelationRepository.save(entity);
  }

  public void deleteById(Long relationId) {
    if (!transportCompanyRelationRepository.existsById(relationId)) {
      throw new IdNotFoundException(relationId);
    }
    transportCompanyRelationRepository.deleteById(relationId);
  }

  void validateRelationOverlaps(TransportCompanyRelation transportCompanyRelation, boolean isUpdateTransportCompanyRelation) {
    List<TransportCompanyRelation> relationOverlaps = findRelationOverlaps(transportCompanyRelation);
    boolean isSelfOverlapping = false;
    if (isUpdateTransportCompanyRelation) {
      isSelfOverlapping = relationOverlaps.stream()
          .anyMatch(relation -> relation.getId().equals(transportCompanyRelation.getId()));
    }
    if (relationOverlaps.size() == 1 && !isSelfOverlapping || relationOverlaps.size() > 1) {
      throw new TransportCompanyRelationConflictException(transportCompanyRelation, relationOverlaps);
    }
  }

  List<TransportCompanyRelation> findRelationOverlaps(TransportCompanyRelation relation) {
    return transportCompanyRelationRepository.findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndSboid(
        relation.getValidFrom(), relation.getValidTo(), relation.getSboid());
  }

  public TransportCompanyRelation findById(Long id) {
    return transportCompanyRelationRepository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

}
