package ch.sbb.prm.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointAttributeType;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.model.referencepoint.ReadReferencePointVersionModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.ContactPointTestData;
import ch.sbb.prm.directory.ParkingLotTestData;
import ch.sbb.prm.directory.PlatformTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.ToiletTestData;
import ch.sbb.prm.directory.controller.model.PrmObjectRequestParams;
import ch.sbb.prm.directory.entity.ContactPointVersion;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.exception.ElementTypeDoesNotExistException;
import ch.sbb.prm.directory.exception.ReducedVariantException;
import ch.sbb.prm.directory.repository.ContactPointRepository;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.repository.ToiletRepository;
import ch.sbb.prm.directory.search.ReferencePointSearchRestrictions;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

class ReferencePointServiceTest extends BasePrmServiceTest {

  private final ReferencePointService referencePointService;
  private final ReferencePointRepository referencePointRepository;
  private final RelationService relationService;
  private final ToiletRepository toiletRepository;
  private final PlatformRepository platformRepository;
  private final StopPointRepository stopPointRepository;
  private final ParkingLotRepository parkingLotRepository;
  private final ContactPointRepository contactPointRepository;

  @Autowired
  ReferencePointServiceTest(ReferencePointService referencePointService, ReferencePointRepository referencePointRepository,
      RelationService relationService,
      ToiletRepository toiletRepository, PlatformRepository platformRepository,
      StopPointRepository stopPointRepository, ParkingLotRepository parkingLotRepository,
      ContactPointRepository contactPointRepository,
      SharedServicePointRepository sharedServicePointRepository,
      PrmLocationService prmLocationService) {
    super(sharedServicePointRepository, prmLocationService);
    this.referencePointService = referencePointService;
    this.referencePointRepository = referencePointRepository;
    this.relationService = relationService;
    this.toiletRepository = toiletRepository;
    this.platformRepository = platformRepository;
    this.stopPointRepository = stopPointRepository;
    this.parkingLotRepository = parkingLotRepository;
    this.contactPointRepository = contactPointRepository;
  }

  @Test
  void shouldCreateReferencePoint() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    createAndSavePlatformVersion(PARENT_SERVICE_POINT_SLOID);
    createAndSaveToiletVersion(PARENT_SERVICE_POINT_SLOID);
    createAndSaveContactPointVersion(PARENT_SERVICE_POINT_SLOID);
    createAndSaveParkingLotVersion(PARENT_SERVICE_POINT_SLOID);

    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when
    referencePointService.createReferencePoint(referencePointVersion);

    //then
    List<RelationVersion> relations = relationService
        .getRelationsByParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    assertThat(relations).hasSize(4);
    assertThat(relations.stream().map(RelationVersion::getReferencePointElementType))
        .containsExactlyInAnyOrder(ReferencePointElementType.PLATFORM, ReferencePointElementType.CONTACT_POINT, ReferencePointElementType.TOILET, ReferencePointElementType.PARKING_LOT);
  }

  @Test
  void shouldNotCreateReferencePointWhenStopPointIsReduced() {
    //given
    String parentServicePointSloid = "ch:1:sloid:70000";
    StopPointVersion stopPointVersion = StopPointTestData.builderVersion1().meansOfTransport(Set.of(MeanOfTransport.BUS)).build();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointRepository.save(stopPointVersion);
    createAndSavePlatformVersion(parentServicePointSloid);
    createAndSaveToiletVersion(parentServicePointSloid);
    createAndSaveContactPointVersion(parentServicePointSloid);
    createAndSaveParkingLotVersion(parentServicePointSloid);

    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(parentServicePointSloid);

    //when
    ReducedVariantException result = assertThrows(
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

  @Test
  void shouldFindReferencePointByParentSloid() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);

    createAndSavePlatformVersion(PARENT_SERVICE_POINT_SLOID);
    createAndSaveToiletVersion(PARENT_SERVICE_POINT_SLOID);
    createAndSaveContactPointVersion(PARENT_SERVICE_POINT_SLOID);
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

  @Test
  void shouldCreateOverviewForReferencePointByParentSloid() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);

    createAndSavePlatformVersion(PARENT_SERVICE_POINT_SLOID);
    createAndSaveToiletVersion(PARENT_SERVICE_POINT_SLOID);
    createAndSaveContactPointVersion(PARENT_SERVICE_POINT_SLOID);
    createAndSaveParkingLotVersion(PARENT_SERVICE_POINT_SLOID);

    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when
    referencePointService.createReferencePoint(referencePointVersion);

    //then
    List<ReadReferencePointVersionModel> result =
        referencePointService.buildOverview(referencePointService.findByParentServicePointSloid(PARENT_SERVICE_POINT_SLOID));

    assertThat(result).hasSize(1);
  }

  @Test
  void shouldCreateOverviewForReferencePointWithMultipleReferencePoints() {
    String sloid = "ch:1:sloid:76332:103";
    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(8576332);

    // Version 1
    referencePointRepository.save(ReferencePointVersion.builder()
        .sloid(sloid)
        .status(Status.VALIDATED)
        .number(servicePointNumber)
        .validFrom(LocalDate.of(2024, 2, 19))
        .validTo(LocalDate.of(2024, 2, 2))
        .designation("Hermione")
        .additionalInformation(null)
        .mainReferencePoint(false)
        .parentServicePointSloid(PARENT_SERVICE_POINT_SLOID)
        .referencePointType(ReferencePointAttributeType.ASSISTANCE_POINT)
        .build());

    // Mad Eye Moodi - Version 1
    String sloidMoodi = "ch:1:sloid:76332:100";
    referencePointRepository.save(ReferencePointVersion.builder()
        .sloid(sloidMoodi)
        .status(Status.VALIDATED)
        .number(servicePointNumber)
        .validFrom(LocalDate.of(2024, 2, 19))
        .validTo(LocalDate.of(2025, 2, 18))
        .designation("Mad Eye Moodi")
        .additionalInformation(null)
        .mainReferencePoint(false)
        .parentServicePointSloid(PARENT_SERVICE_POINT_SLOID)
        .referencePointType(ReferencePointAttributeType.PLATFORM)
        .build());

    // Version 4
    referencePointRepository.save(ReferencePointVersion.builder()
        .sloid(sloid)
        .status(Status.VALIDATED)
        .number(servicePointNumber)
        .validFrom(LocalDate.of(2027, 3, 5))
        .validTo(LocalDate.of(2029, 3, 4))
        .designation("Hermione")
        .additionalInformation("jup")
        .mainReferencePoint(false)
        .parentServicePointSloid(PARENT_SERVICE_POINT_SLOID)
        .referencePointType(ReferencePointAttributeType.MAIN_STATION_ENTRANCE)
        .build());

    // Mad Eye Moodi - Version 2
    referencePointRepository.save(ReferencePointVersion.builder()
        .sloid(sloidMoodi)
        .status(Status.VALIDATED)
        .number(servicePointNumber)
        .validFrom(LocalDate.of(2025, 2, 19))
        .validTo(LocalDate.of(2029, 2, 18))
        .designation("Mad Eye Moodi")
        .additionalInformation("Viola")
        .mainReferencePoint(false)
        .parentServicePointSloid(PARENT_SERVICE_POINT_SLOID)
        .referencePointType(ReferencePointAttributeType.PLATFORM)
        .build());

    //then
    List<ReferencePointVersion> versionsByParent = referencePointService.findByParentServicePointSloid(
        PARENT_SERVICE_POINT_SLOID);
    List<ReadReferencePointVersionModel> result = referencePointService.buildOverview(versionsByParent);

    assertThat(result).hasSize(2);
  }


  @Test
  void testCheckExistsForReferencePoint_DoesNotExist() {
    String sloid = "ch:1:sloid:76646:1";
    assertThrows(ElementTypeDoesNotExistException.class, () -> {
      referencePointService.checkReferencePointExists(sloid, "REFERENCE_POINT");
    });
  }

  private void createAndSaveParkingLotVersion(String parentServicePointSloid) {
    ParkingLotVersion parkingLot = ParkingLotTestData.getParkingLotVersion();
    parkingLot.setParentServicePointSloid(parentServicePointSloid);
    parkingLot.setSloid("ch:1:sloid:70000:5");
    parkingLotRepository.save(parkingLot);
  }

  private void createAndSaveContactPointVersion(String parentServicePointSloid) {
    ContactPointVersion contactPointVersion = ContactPointTestData.getContactPointVersion();
    contactPointVersion.setParentServicePointSloid(parentServicePointSloid);
    contactPointVersion.setSloid("ch:1:sloid:70000:4");
    contactPointRepository.save(contactPointVersion);
  }

  private void createAndSaveToiletVersion(String parentServicePointSloid) {
    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    toiletVersion.setParentServicePointSloid(parentServicePointSloid);
    toiletVersion.setSloid("ch:1:sloid:70000:3");
    toiletRepository.save(toiletVersion);
  }

  private void createAndSavePlatformVersion(String parentServicePointSloid) {
    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    platformVersion.setParentServicePointSloid(parentServicePointSloid);
    platformVersion.setSloid("ch:1:sloid:70000:1");
    platformRepository.saveAndFlush(platformVersion);
  }

}
