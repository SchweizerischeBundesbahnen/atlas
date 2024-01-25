package ch.sbb.prm.directory.service;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.PARKING_LOT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.ParkingLotTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.exception.StopPointDoesNotExistException;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class ParkingLotServiceTest extends BasePrmServiceTest {

  private final ParkingLotService parkingLotService;
  private final ParkingLotRepository parkingLotRepository;
  private final RelationRepository relationRepository;
  private final StopPointRepository stopPointRepository;
  private final ReferencePointRepository referencePointRepository;

  @MockBean
  private LocationClient locationClient;

  @Autowired
  ParkingLotServiceTest(ParkingLotService parkingLotService,
      ParkingLotRepository parkingLotRepository,
      RelationRepository relationRepository,
      StopPointRepository stopPointRepository,
      ReferencePointRepository referencePointRepository,
      SharedServicePointRepository sharedServicePointRepository) {
    super(sharedServicePointRepository);
    this.parkingLotService = parkingLotService;
    this.parkingLotRepository = parkingLotRepository;
    this.relationRepository = relationRepository;
    this.stopPointRepository = stopPointRepository;
    this.referencePointRepository = referencePointRepository;
  }

  @Test
  void shouldNotCreateParkingLotWhenStopPointDoesNotExist() {
    //given
    ParkingLotVersion parkingLot = ParkingLotTestData.getParkingLotVersion();
    parkingLot.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when & then
    assertThrows(StopPointDoesNotExistException.class,
        () -> parkingLotService.createParkingLot(parkingLot)).getLocalizedMessage();
  }

  @Test
  void shouldCreateParkingLotWhenNoReferencePointExists() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ParkingLotVersion parkingLot = ParkingLotTestData.getParkingLotVersion();
    parkingLot.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when
    parkingLotService.createParkingLot(parkingLot);

    //then
    List<ParkingLotVersion> parkingLotVersions = parkingLotRepository
        .findByParentServicePointSloid(parkingLot.getParentServicePointSloid());
    assertThat(parkingLotVersions).hasSize(1);
    assertThat(parkingLotVersions.get(0).getParentServicePointSloid()).isEqualTo(parkingLot.getParentServicePointSloid());
    List<RelationVersion> relationVersions = relationRepository
        .findAllByParentServicePointSloid(parkingLot.getParentServicePointSloid());
    assertThat(relationVersions).isEmpty();
    verify(locationClient, times(1)).claimSloid(argThat(
        claimSloidRequestModel -> claimSloidRequestModel.sloidType() == SloidType.PARKING_LOT
            && Objects.equals(claimSloidRequestModel.sloid(), "ch:1:sloid:12345:1")));
  }

  @Test
  void shouldCreateParkingLotWhenReferencePointExists() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);
    ParkingLotVersion parkingLot = ParkingLotTestData.getParkingLotVersion();
    parkingLot.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when
    parkingLotService.createParkingLot(parkingLot);

    //then
    List<ParkingLotVersion> parkingLotVersions = parkingLotRepository
        .findByParentServicePointSloid(parkingLot.getParentServicePointSloid());
    assertThat(parkingLotVersions).hasSize(1);
    assertThat(parkingLotVersions.get(0).getParentServicePointSloid()).isEqualTo(parkingLot.getParentServicePointSloid());
    List<RelationVersion> relationVersions = relationRepository
        .findAllByParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    assertThat(relationVersions).hasSize(1);
    assertThat(relationVersions.get(0).getParentServicePointSloid()).isEqualTo(PARENT_SERVICE_POINT_SLOID);
    assertThat(relationVersions.get(0).getReferencePointElementType()).isEqualTo(PARKING_LOT);
    verify(locationClient, times(1)).claimSloid(argThat(
        claimSloidRequestModel -> claimSloidRequestModel.sloidType() == SloidType.PARKING_LOT
            && Objects.equals(claimSloidRequestModel.sloid(), "ch:1:sloid:12345:1")));
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
    verify(locationClient, times(1)).claimSloid(argThat(
        claimSloidRequestModel -> claimSloidRequestModel.sloidType() == SloidType.PARKING_LOT
            && Objects.equals(claimSloidRequestModel.sloid(), "ch:1:sloid:12345:1")));
  }

}
