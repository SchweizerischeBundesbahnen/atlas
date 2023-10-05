package ch.sbb.prm.directory.service;

import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.INFORMATION_DESK;

import ch.sbb.prm.directory.entity.InformationDeskVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.repository.InformationDeskRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InformationDeskService extends RelatableService<InformationDeskVersion> {

  private final InformationDeskRepository informationDeskRepository;

  public InformationDeskService(InformationDeskRepository informationDeskRepository, StopPlaceService stopPlaceService,
      RelationService relationRepository, ReferencePointRepository referencePointRepository) {
    super(stopPlaceService,relationRepository,referencePointRepository);
    this.informationDeskRepository = informationDeskRepository;
  }

  @Override
  protected ReferencePointElementType getReferencePointElementType() {
    return INFORMATION_DESK;
  }
  public List<InformationDeskVersion> getAllInformationDesks() {
   return informationDeskRepository.findAll();
  }

  public void createInformationDesk(InformationDeskVersion version) {
    createRelation(version);
    informationDeskRepository.save(version);
  }

}
