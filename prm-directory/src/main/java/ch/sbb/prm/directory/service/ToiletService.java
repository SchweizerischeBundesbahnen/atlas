package ch.sbb.prm.directory.service;

import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.TOILET;

import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import ch.sbb.prm.directory.repository.ToiletRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ToiletService extends BaseRelationConnectionService<ToiletVersion>{

  private final ToiletRepository toiletRepository;

  public ToiletService(ToiletRepository toiletRepository, StopPlaceRepository stopPlaceRepository,
      RelationRepository relationRepository, ReferencePointRepository referencePointRepository) {
    super(stopPlaceRepository,relationRepository,referencePointRepository);
    this.toiletRepository = toiletRepository;
  }

  public List<ToiletVersion> getAllToilets() {
   return toiletRepository.findAll();
  }

  public void createToilet(ToiletVersion version) {
    checkStopPlaceExists(version.getParentServicePointSloid());
    createRelation(version, TOILET);
    toiletRepository.save(version);
  }

}
