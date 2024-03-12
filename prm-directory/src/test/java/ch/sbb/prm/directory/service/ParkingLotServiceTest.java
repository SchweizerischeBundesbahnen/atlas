package ch.sbb.prm.directory.service;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.PARKING_LOT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.RecordingStatus;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.model.parkinglot.ParkingLotOverviewModel;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.ParkingLotTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.controller.model.PrmObjectRequestParams;
import ch.sbb.prm.directory.entity.*;
import ch.sbb.prm.directory.exception.ElementTypeDoesNotExistException;
import ch.sbb.prm.directory.exception.StopPointDoesNotExistException;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.search.ParkingLotSearchRestrictions;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

class ParkingLotServiceTest extends BasePrmServiceTest {

  private final ParkingLotService parkingLotService;
  private final ParkingLotRepository parkingLotRepository;
  private final RelationRepository relationRepository;
  private final StopPointRepository stopPointRepository;
  private final ReferencePointRepository referencePointRepository;

  @Autowired
  ParkingLotServiceTest(ParkingLotService parkingLotService,
      ParkingLotRepository parkingLotRepository,
      RelationRepository relationRepository,
      StopPointRepository stopPointRepository,
      ReferencePointRepository referencePointRepository,
      SharedServicePointRepository sharedServicePointRepository,
      PrmLocationService prmLocationService) {
    super(sharedServicePointRepository, prmLocationService);
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
    verify(prmLocationService, times(1)).allocateSloid(any(), eq(SloidType.PARKING_LOT));
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
    verify(prmLocationService, times(1)).allocateSloid(any(), eq(SloidType.PARKING_LOT));
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
    verify(prmLocationService, times(1)).allocateSloid(any(), eq(SloidType.PARKING_LOT));
  }

  @Test
  void shouldFindBySloid() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);

    ParkingLotVersion parkingLotVersion = ParkingLotTestData.getParkingLotVersion();
    String parentServicePointSloid = stopPointVersion.getSloid();
    parkingLotVersion.setParentServicePointSloid(parentServicePointSloid);
    parkingLotService.save(parkingLotVersion);

    //when
    ParkingLotSearchRestrictions wrongSloidRequest = ParkingLotSearchRestrictions.builder().pageable(Pageable.ofSize(1))
        .prmObjectRequestParams(
            PrmObjectRequestParams.builder().sloids(List.of("ch:1:sloid:asd")).build()).build();
    Page<ParkingLotVersion> result = parkingLotService.findAll(wrongSloidRequest);

    //then
    assertThat(result.getTotalElements()).isZero();

    ParkingLotSearchRestrictions correctSloidRequest = ParkingLotSearchRestrictions.builder().pageable(Pageable.ofSize(1))
        .prmObjectRequestParams(
            PrmObjectRequestParams.builder().sloids(List.of(parkingLotVersion.getSloid())).build()).build();
    result = parkingLotService.findAll(correctSloidRequest);
    assertThat(result.getContent()).isNotEmpty();
  }

  @Test
  void shouldCreateOverviewForParkingLotsByParentSloid() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);

    ParkingLotVersion parkingLot = ParkingLotTestData.getParkingLotVersion();
    parkingLot.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    parkingLotService.createParkingLot(parkingLot);

    //when
    List<ParkingLotOverviewModel> result =
        parkingLotService.buildOverview(parkingLotService.findByParentServicePointSloid(PARENT_SERVICE_POINT_SLOID));

    //then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getRecordingStatus()).isEqualTo(RecordingStatus.INCOMPLETE);
  }

  @Test
  void shouldFindParkingLotByValidOn() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);

    ParkingLotVersion parkingLot = ParkingLotTestData.getParkingLotVersion();
    parkingLot.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    parkingLot = parkingLotService.createParkingLot(parkingLot);

    //when
    Page<ParkingLotVersion> result =
        parkingLotService.findAll(ParkingLotSearchRestrictions.builder()
            .pageable(Pageable.ofSize(10))
            .prmObjectRequestParams(PrmObjectRequestParams.builder()
                .validOn(parkingLot.getValidFrom())
                .build()).build());

    //then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().getFirst().getId()).isEqualTo(parkingLot.getId());
  }

  @Test
  void shouldFindParkingLotBySloid() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);

    ParkingLotVersion parkingLot = ParkingLotTestData.getParkingLotVersion();
    parkingLot.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    parkingLot = parkingLotService.createParkingLot(parkingLot);

    //when
    Page<ParkingLotVersion> result =
        parkingLotService.findAll(ParkingLotSearchRestrictions.builder()
            .pageable(Pageable.ofSize(10))
            .prmObjectRequestParams(PrmObjectRequestParams.builder()
                .sloid(parkingLot.getSloid())
                .build()).build());

    //then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().getFirst().getId()).isEqualTo(parkingLot.getId());
  }

  @Test
  void shouldFindParkingLotByParentSloid() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);

    ParkingLotVersion parkingLot = ParkingLotTestData.getParkingLotVersion();
    parkingLot.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    parkingLot = parkingLotService.createParkingLot(parkingLot);

    //when
    Page<ParkingLotVersion> result =
        parkingLotService.findAll(ParkingLotSearchRestrictions.builder()
            .pageable(Pageable.ofSize(10))
            .prmObjectRequestParams(PrmObjectRequestParams.builder()
                .parentServicePointSloid(parkingLot.getParentServicePointSloid())
                .build()).build());

    //then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().getFirst().getId()).isEqualTo(parkingLot.getId());
  }

  @Test
  void testCheckParkingLotExists_Exists() {
    ParkingLotVersion parkingLotVersion = ParkingLotTestData.getParkingLotVersion();
    parkingLotVersion.setSloid("ch:1:sloid:12345:1");
    parkingLotRepository.saveAndFlush(parkingLotVersion);

    assertDoesNotThrow(() -> parkingLotService.checkParkingLotExists("ch:1:sloid:12345:1", PARKING_LOT.name()));
  }

  @Test
  void testCheckParkingLotExists_DoesNotExist() {
    assertThrows(ElementTypeDoesNotExistException.class,
        () -> parkingLotService.checkParkingLotExists("ch:1:sloid:12345:1", ReferencePointElementType.PARKING_LOT.name()));
  }

  @Test
  void shouldReturnIncompleteStatus() {
    ParkingLotVersion parkingLotVersion = ParkingLotTestData.getParkingLotVersion();

    RecordingStatus result = parkingLotVersion.getRecordingStatus();
    assertThat(result).isEqualTo(RecordingStatus.INCOMPLETE);
  }

  @Test
  void shouldReturnCompleteStatus() {
    ParkingLotVersion parkingLotVersion = ParkingLotTestData.getParkingLotVersion();
    parkingLotVersion.setPlacesAvailable(BooleanOptionalAttributeType.YES);
    parkingLotVersion.setPrmPlacesAvailable(BooleanOptionalAttributeType.YES);

    RecordingStatus result = parkingLotVersion.getRecordingStatus();
    assertThat(result).isEqualTo(RecordingStatus.COMPLETE);
  }

}
