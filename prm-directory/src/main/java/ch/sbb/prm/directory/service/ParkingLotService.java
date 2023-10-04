package ch.sbb.prm.directory.service;

import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class ParkingLotService {

  private final ParkingLotRepository parkingLotRepository;

  public List<ParkingLotVersion> getAllParkingLots() {
   return parkingLotRepository.findAll();
  }

  public List<ParkingLotVersion> getByServicePointParentSloid(String parentServicePointSloid){
    return parkingLotRepository.findByParentServicePointSloid(parentServicePointSloid);
  }

}
