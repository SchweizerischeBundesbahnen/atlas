package ch.sbb.prm.directory.service;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.SharedServicePointVersionModel;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.ParkingLotTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.exception.StopPointDoesNotExistsException;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.PARKING_LOT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@IntegrationTest
@Transactional
class ParkingLotServiceTest {

  private final ReferencePointRepository referencePointRepository;
  private final RelationRepository relationRepository;

  private final StopPointRepository stopPointRepository;

  private final ParkingLotRepository parkingLotRepository;

  private final ParkingLotService parkingLotService;

  @Autowired
  ParkingLotServiceTest(ReferencePointRepository referencePointRepository,
      RelationRepository relationRepository, StopPointRepository stopPointRepository,
      ParkingLotRepository parkingLotRepository, ParkingLotService parkingLotService) {
    this.referencePointRepository = referencePointRepository;
    this.relationRepository = relationRepository;
    this.stopPointRepository = stopPointRepository;
    this.parkingLotRepository = parkingLotRepository;
    this.parkingLotService = parkingLotService;
  }

  @Test
  void shouldNotCreateParkingLotWhenStopPointDoesNotExists() {
    //given
    String parentServicePointSloid="ch:1:sloid:70000";
    ParkingLotVersion parkingLot = ParkingLotTestData.getParkingLotVersion();
    parkingLot.setParentServicePointSloid(parentServicePointSloid);
    SharedServicePointVersionModel sharedServicePointVersionModel = new SharedServicePointVersionModel(parentServicePointSloid, Collections.singleton("sboid"), Collections.singleton(""));

    //when & then
    assertThrows(StopPointDoesNotExistsException.class,
        () -> parkingLotService.createParkingLot(parkingLot, sharedServicePointVersionModel)).getLocalizedMessage();
  }

  @Test
  void shouldCreateParkingLotWhenNoReferencePointExists() {
    //given
    String parentServicePointSloid="ch:1:sloid:70000";
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointRepository.save(stopPointVersion);
    ParkingLotVersion parkingLot = ParkingLotTestData.getParkingLotVersion();
    parkingLot.setParentServicePointSloid(parentServicePointSloid);
    SharedServicePointVersionModel sharedServicePointVersionModel = new SharedServicePointVersionModel(parentServicePointSloid, Collections.singleton("sboid"), Collections.singleton(""));
    //when
    parkingLotService.createParkingLot(parkingLot, sharedServicePointVersionModel);

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
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(parentServicePointSloid);
    referencePointRepository.save(referencePointVersion);
    ParkingLotVersion parkingLot = ParkingLotTestData.getParkingLotVersion();
    parkingLot.setParentServicePointSloid(parentServicePointSloid);
    SharedServicePointVersionModel sharedServicePointVersionModel = new SharedServicePointVersionModel(parentServicePointSloid, Collections.singleton("sboid"), Collections.singleton(""));

    //when
    parkingLotService.createParkingLot(parkingLot, sharedServicePointVersionModel);

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

  @Test
  void shouldNotCreateParkingLotRelationWhenStopPointIsReduced() {
    //given
    String parentServicePointSloid = "ch:1:sloid:70000";
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointVersion.setMeansOfTransport(Set.of(MeanOfTransport.BUS));
    stopPointRepository.save(stopPointVersion);
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
    assertThat(relationVersions).isEmpty();
  }

}