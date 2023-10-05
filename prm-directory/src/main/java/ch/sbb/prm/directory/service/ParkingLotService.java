package ch.sbb.prm.directory.service;

import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.PARKING_LOT;

import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ParkingLotService extends BaseRelationConnectionService<ParkingLotVersion>{

  private final ParkingLotRepository parkingLotRepository;

  public ParkingLotService(ParkingLotRepository parkingLotRepository, StopPlaceRepository stopPlaceRepository,
      RelationRepository relationRepository, ReferencePointRepository referencePointRepository) {
    super(stopPlaceRepository,relationRepository,referencePointRepository);
    this.parkingLotRepository = parkingLotRepository;
  }

  public List<ParkingLotVersion> getAllParkingLots() {
   return parkingLotRepository.findAll();
  }

  public void createParkingLot(ParkingLotVersion version) {
    checkStopPlaceExists(version.getParentServicePointSloid());
    createRelation(version, PARKING_LOT);
    parkingLotRepository.save(version);
  }

}
