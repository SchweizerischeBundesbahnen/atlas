package ch.sbb.prm.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.prm.directory.ParkingLotTestData;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class ParkingLotRepositoryTest {

  private final ParkingLotRepository parkingLotRepository;

  @Autowired
  ParkingLotRepositoryTest(ParkingLotRepository parkingLotRepository) {
    this.parkingLotRepository = parkingLotRepository;
  }

  @BeforeEach()
  void initDB() {
    parkingLotRepository.save(ParkingLotTestData.getParkingLotVersion());
  }

  @Test
  void shouldReturnParkingLots() {
    //when
   List<ParkingLotVersion> result = parkingLotRepository.findAll();
   //then
   assertThat(result).hasSize(1);
  }

}