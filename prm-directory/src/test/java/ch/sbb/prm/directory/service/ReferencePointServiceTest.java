package ch.sbb.prm.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.InformationDeskTestData;
import ch.sbb.prm.directory.ParkingLotTestData;
import ch.sbb.prm.directory.PlatformTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.TicketCounterTestData;
import ch.sbb.prm.directory.ToiletTestData;
import ch.sbb.prm.directory.controller.model.PrmObjectRequestParams;
import ch.sbb.prm.directory.entity.InformationDeskVersion;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.entity.TicketCounterVersion;
import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.exception.ReducedVariantException;
import ch.sbb.prm.directory.repository.InformationDeskRepository;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.repository.TicketCounterRepository;
import ch.sbb.prm.directory.repository.ToiletRepository;
import ch.sbb.prm.directory.search.ReferencePointSearchRestrictions;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

class ReferencePointServiceTest extends BasePrmServiceTest {

  private final ReferencePointService referencePointService;
  private final RelationService relationService;
  private final ToiletRepository toiletRepository;
  private final PlatformRepository platformRepository;
  private final StopPointRepository stopPointRepository;
  private final ParkingLotRepository parkingLotRepository;
  private final TicketCounterRepository ticketCounterRepository;
  private final InformationDeskRepository informationDeskRepository;

  @Autowired
  ReferencePointServiceTest(ReferencePointService referencePointService, RelationService relationService,
                            ToiletRepository toiletRepository, PlatformRepository platformRepository,
                            StopPointRepository stopPointRepository, ParkingLotRepository parkingLotRepository,
                            TicketCounterRepository ticketCounterRepository,
                            InformationDeskRepository informationDeskRepository,
                            SharedServicePointRepository sharedServicePointRepository) {
    super(sharedServicePointRepository);
    this.referencePointService = referencePointService;
    this.relationService = relationService;
    this.toiletRepository = toiletRepository;
    this.platformRepository = platformRepository;
    this.stopPointRepository = stopPointRepository;
    this.parkingLotRepository = parkingLotRepository;
    this.ticketCounterRepository = ticketCounterRepository;
    this.informationDeskRepository = informationDeskRepository;
  }

  @Test
  void shouldCreateReferencePoint() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    createAndSavePlatformVersion(PARENT_SERVICE_POINT_SLOID);
    createAndSaveTicketCounterVersion(PARENT_SERVICE_POINT_SLOID);
    createAndSaveToiletVersion(PARENT_SERVICE_POINT_SLOID);
    createAndSaveInformationDeskVersion(PARENT_SERVICE_POINT_SLOID);
    createAndSaveParkingLotVersion(PARENT_SERVICE_POINT_SLOID);

    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when
    referencePointService.createReferencePoint(referencePointVersion);

    //then
    List<RelationVersion> relations = relationService
            .getRelationsByParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    assertThat(relations).hasSize(5);
    assertThat(relations.stream().map(RelationVersion::getReferencePointElementType))
            .containsExactlyInAnyOrder(ReferencePointElementType.values());
  }

  @Test
  void shouldNotCreateReferencePointWhenStopPointIsReduced() {
    //given
    String parentServicePointSloid = "ch:1:sloid:70000";
    StopPointVersion stopPointVersion = StopPointTestData.builderVersion1().meansOfTransport(Set.of(MeanOfTransport.BUS)).build();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointRepository.save(stopPointVersion);
    createAndSavePlatformVersion(parentServicePointSloid);
    createAndSaveTicketCounterVersion(parentServicePointSloid);
    createAndSaveToiletVersion(parentServicePointSloid);
    createAndSaveInformationDeskVersion(parentServicePointSloid);
    createAndSaveParkingLotVersion(parentServicePointSloid);

    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(parentServicePointSloid);

    //when
    ReducedVariantException result = Assertions.assertThrows(
        ReducedVariantException.class,
        () -> referencePointService.createReferencePoint(referencePointVersion));

    //then
    assertThat(result).isNotNull();
    ErrorResponse errorResponse = result.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(412);
    assertThat(errorResponse.getMessage()).isEqualTo("Object creation not allowed for reduced variant!");
    List<RelationVersion> relations = relationService.getRelationsByParentServicePointSloid(
        parentServicePointSloid);
    assertThat(relations).isEmpty();
  }

  private void createAndSaveParkingLotVersion(String parentServicePointSloid) {
    ParkingLotVersion parkingLot = ParkingLotTestData.getParkingLotVersion();
    parkingLot.setParentServicePointSloid(parentServicePointSloid);
    parkingLot.setSloid("ch:1:sloid:70000:5");
    parkingLotRepository.save(parkingLot);
  }

  private void createAndSaveInformationDeskVersion(String parentServicePointSloid) {
    InformationDeskVersion informationDesk = InformationDeskTestData.getInformationDeskVersion();
    informationDesk.setParentServicePointSloid(parentServicePointSloid);
    informationDesk.setSloid("ch:1:sloid:70000:4");
    informationDeskRepository.save(informationDesk);
  }

  private void createAndSaveToiletVersion(String parentServicePointSloid) {
    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    toiletVersion.setParentServicePointSloid(parentServicePointSloid);
    toiletVersion.setSloid("ch:1:sloid:70000:3");
    toiletRepository.save(toiletVersion);
  }

  private void createAndSaveTicketCounterVersion(String parentServicePointSloid) {
    TicketCounterVersion ticketCounterversion = TicketCounterTestData.getTicketCounterVersion();
    ticketCounterversion.setParentServicePointSloid(parentServicePointSloid);
    ticketCounterversion.setSloid("ch:1:sloid:70000:2");
    ticketCounterRepository.save(ticketCounterversion);
  }

  private void createAndSavePlatformVersion(String parentServicePointSloid) {
    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    platformVersion.setParentServicePointSloid(parentServicePointSloid);
    platformVersion.setSloid("ch:1:sloid:70000:1");
    platformRepository.saveAndFlush(platformVersion);
  }

  @Test
  void shouldFindReferencePointByParentSloid() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);

    createAndSavePlatformVersion(PARENT_SERVICE_POINT_SLOID);
    createAndSaveTicketCounterVersion(PARENT_SERVICE_POINT_SLOID);
    createAndSaveToiletVersion(PARENT_SERVICE_POINT_SLOID);
    createAndSaveInformationDeskVersion(PARENT_SERVICE_POINT_SLOID);
    createAndSaveParkingLotVersion(PARENT_SERVICE_POINT_SLOID);

    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when
    referencePointService.createReferencePoint(referencePointVersion);

    //then
    Page<ReferencePointVersion> result = referencePointService.findAll(
        ReferencePointSearchRestrictions.builder().pageable(Pageable.ofSize(1)).prmObjectRequestParams(
            PrmObjectRequestParams.builder().parentServicePointSloids(List.of("ch:1:unknownsloid")).build()).build());
    assertThat(result.getTotalElements()).isZero();
    assertThat(result.getContent()).isEmpty();

     result = referencePointService.findAll(
        ReferencePointSearchRestrictions.builder().pageable(Pageable.ofSize(1)).prmObjectRequestParams(
            PrmObjectRequestParams.builder().parentServicePointSloids(List.of(PARENT_SERVICE_POINT_SLOID)).build()).build());
    assertThat(result.getTotalElements()).isOne();
    assertThat(result.getContent()).isNotEmpty();
  }

}