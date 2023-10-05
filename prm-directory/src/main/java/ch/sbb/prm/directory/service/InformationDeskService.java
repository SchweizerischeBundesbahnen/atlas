package ch.sbb.prm.directory.service;

import ch.sbb.prm.directory.entity.InformationDeskVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.repository.InformationDeskRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InformationDeskService extends BaseRelationConnectionService<InformationDeskVersion>{

  private final InformationDeskRepository informationDeskRepository;

  public InformationDeskService(InformationDeskRepository informationDeskRepository, StopPlaceRepository stopPlaceRepository,
      RelationRepository relationRepository, ReferencePointRepository referencePointRepository) {
    super(stopPlaceRepository,relationRepository,referencePointRepository);
    this.informationDeskRepository = informationDeskRepository;
  }

  public List<InformationDeskVersion> getAllInformationDesks() {
   return informationDeskRepository.findAll();
  }

  public void createInformationDesk(InformationDeskVersion version) {
    checkStopPlaceExists(version.getParentServicePointSloid());
    createRelation(version, ReferencePointElementType.INFORMATION_DESK);
    informationDeskRepository.save(version);
  }

}
