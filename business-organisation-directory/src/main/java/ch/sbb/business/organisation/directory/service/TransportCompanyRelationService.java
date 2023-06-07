package ch.sbb.business.organisation.directory.service;

import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.business.organisation.directory.entity.TransportCompanyRelation;
import ch.sbb.atlas.business.organisation.exception.SboidNotFoundException;
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

  public TransportCompanyRelation save(TransportCompanyRelation entity) {
    if (businessOrganisationService.findBusinessOrganisationVersions(entity.getSboid()).isEmpty()) {
      throw new SboidNotFoundException(entity.getSboid());
    }
    if (!transportCompanyService.existsById(entity.getTransportCompany().getId())) {
      throw new TransportCompanyNotFoundException(entity.getTransportCompany().getId());
    }
    validateRelationOverlaps(entity);
    return transportCompanyRelationRepository.save(entity);
  }

  public void deleteById(Long relationId) {
    if (!transportCompanyRelationRepository.existsById(relationId)) {
      throw new IdNotFoundException(relationId);
    }
    transportCompanyRelationRepository.deleteById(relationId);
  }

  void validateRelationOverlaps(TransportCompanyRelation relation) {
    List<TransportCompanyRelation> relationOverlaps = findRelationOverlaps(relation);
    if (!relationOverlaps.isEmpty()) {
      throw new TransportCompanyRelationConflictException(relation, relationOverlaps);
    }
  }

  List<TransportCompanyRelation> findRelationOverlaps(TransportCompanyRelation relation) {
    return transportCompanyRelationRepository.findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndSboid(
        relation.getValidFrom(), relation.getValidTo(), relation.getSboid());
  }

}
