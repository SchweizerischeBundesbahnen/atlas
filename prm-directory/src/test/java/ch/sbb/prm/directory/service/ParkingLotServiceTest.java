package ch.sbb.prm.directory.service;

import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.PARKING_LOT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.prm.directory.ParkingLotTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPlaceTestData;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class ParkingLotServiceTest {

  private final ReferencePointRepository referencePointRepository;
  private final RelationRepository relationRepository;

  private final StopPlaceRepository stopPlaceRepository;

  private final ParkingLotRepository parkingLotRepository;

  private final ParkingLotService parkingLotService;

  @Autowired
  ParkingLotServiceTest(ReferencePointRepository referencePointRepository,
      RelationRepository relationRepository, StopPlaceRepository stopPlaceRepository,
      ParkingLotRepository parkingLotRepository, ParkingLotService parkingLotService) {
    this.referencePointRepository = referencePointRepository;
    this.relationRepository = relationRepository;
    this.stopPlaceRepository = stopPlaceRepository;
    this.parkingLotRepository = parkingLotRepository;
    this.parkingLotService = parkingLotService;
  }

  @Test
  void shouldNotCreateParkingLotWhenStopPlaceDoesNotExists() {
    //given
    String parentServicePointSloid="ch:1:sloid:70000";
    ParkingLotVersion parkingLot = ParkingLotTestData.getParkingLotVersion();
    parkingLot.setParentServicePointSloid(parentServicePointSloid);

    //when & then
    String message = assertThrows(IllegalStateException.class,
        () -> parkingLotService.createParkingLot(parkingLot)).getLocalizedMessage();
    assertThat(message).isEqualTo("StopPlace with sloid [ch:1:sloid:70000] does not exists!");
  }

  @Test
  void shouldCreateParkingLotWhenNoReferencePointExists() {
    //given
    String parentServicePointSloid="ch:1:sloid:70000";
    StopPlaceVersion stopPlaceVersion = StopPlaceTestData.getStopPlaceVersion();
    stopPlaceVersion.setSloid(parentServicePointSloid);
    stopPlaceRepository.save(stopPlaceVersion);
    ParkingLotVersion parkingLot = ParkingLotTestData.getParkingLotVersion();
    parkingLot.setParentServicePointSloid(parentServicePointSloid);
    //when
    parkingLotService.createParkingLot(parkingLot);

    //then
    List<ParkingLotVersion> parkingLotVersions = parkingLotRepository.findByParentServicePointSloid(
        parkingLot.getParentServicePointSloid());
    assertThat(parkingLotVersions).hasSize(1);
    assertThat(parkingLotVersions.get(0).getParentServicePointSloid()).isEqualTo(parkingLot.getParentServicePointSloid());
    List<RelationVersion> relationVersions = relationRepository.findAllByParentServicePointSloid(
        parkingLot.getParentServicePointSloid());
    assertThat(relationVersions).isEmpty();
  }

  @Test
  void shouldCreateParkingLotWhenReferencePointExists() {
    //given
    String parentServicePointSloid = "ch:1:sloid:70000";
    StopPlaceVersion stopPlaceVersion = StopPlaceTestData.getStopPlaceVersion();
    stopPlaceVersion.setSloid(parentServicePointSloid);
    stopPlaceRepository.save(stopPlaceVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(parentServicePointSloid);
    referencePointRepository.save(referencePointVersion);
    ParkingLotVersion parkingLot = ParkingLotTestData.getParkingLotVersion();
    parkingLot.setParentServicePointSloid(parentServicePointSloid);

    //when
    parkingLotService.createParkingLot(parkingLot);

    //then
    List<ParkingLotVersion> parkingLotVersions = parkingLotRepository.findByParentServicePointSloid(
        parkingLot.getParentServicePointSloid());
    assertThat(parkingLotVersions).hasSize(1);
    assertThat(parkingLotVersions.get(0).getParentServicePointSloid()).isEqualTo(parkingLot.getParentServicePointSloid());
    List<RelationVersion> relationVersions = relationRepository.findAllByParentServicePointSloid(
        parentServicePointSloid);
    assertThat(relationVersions).hasSize(1);
    assertThat(relationVersions.get(0).getParentServicePointSloid()).isEqualTo(parentServicePointSloid);
    assertThat(relationVersions.get(0).getReferencePointElementType()).isEqualTo(PARKING_LOT);
  }

}