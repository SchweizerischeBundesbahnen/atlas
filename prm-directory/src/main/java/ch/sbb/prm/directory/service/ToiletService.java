package ch.sbb.prm.directory.service;

import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.TOILET;

import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import ch.sbb.prm.directory.repository.ToiletRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ToiletService extends BaseRelatableService<ToiletVersion> {

  private final ToiletRepository toiletRepository;

  public ToiletService(ToiletRepository toiletRepository, StopPlaceRepository stopPlaceRepository,
      RelationRepository relationRepository, ReferencePointRepository referencePointRepository) {
    super(stopPlaceRepository,relationRepository,referencePointRepository);
    this.toiletRepository = toiletRepository;
  }

  @Override
  protected ReferencePointElementType getReferencePointElementType() {
    return TOILET;
  }

  public List<ToiletVersion> getAllToilets() {
   return toiletRepository.findAll();
  }

  public void createToilet(ToiletVersion version) {
    createRelation(version);
    toiletRepository.save(version);
  }


}
