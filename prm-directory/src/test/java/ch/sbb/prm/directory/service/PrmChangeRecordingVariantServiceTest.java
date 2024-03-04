package ch.sbb.prm.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.BasicAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.ContactPointTestData;
import ch.sbb.prm.directory.ParkingLotTestData;
import ch.sbb.prm.directory.PlatformTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.ToiletTestData;
import ch.sbb.prm.directory.entity.ContactPointVersion;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.repository.ContactPointRepository;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.repository.ToiletRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PrmChangeRecordingVariantServiceTest extends BasePrmServiceTest {

  private final StopPointRepository stopPointRepository;

  private final ReferencePointRepository referencePointRepository;

  private final PlatformRepository platformRepository;

  private final ToiletRepository toiletRepository;

  private final ParkingLotRepository parkingLotRepository;

  private final RelationRepository relationRepository;

  private final ContactPointRepository contactPointRepository;

  private final PrmChangeRecordingVariantService prmChangeRecordingVariantService;

  @Autowired
  PrmChangeRecordingVariantServiceTest(
      SharedServicePointRepository sharedServicePointRepository,
      PrmLocationService prmLocationService, StopPointRepository stopPointRepository,
      ReferencePointRepository referencePointRepository, PlatformRepository platformRepository, ToiletRepository toiletRepository,
      ParkingLotRepository parkingLotRepository, RelationRepository relationRepository,
      ContactPointRepository contactPointRepository, PrmChangeRecordingVariantService prmChangeRecordingVariantService) {
    super(sharedServicePointRepository, prmLocationService);
    this.stopPointRepository = stopPointRepository;
    this.referencePointRepository = referencePointRepository;
    this.platformRepository = platformRepository;
    this.toiletRepository = toiletRepository;
    this.parkingLotRepository = parkingLotRepository;
    this.relationRepository = relationRepository;
    this.contactPointRepository = contactPointRepository;
    this.prmChangeRecordingVariantService = prmChangeRecordingVariantService;
  }

  @Test
  void shouldChangeRecordVariantFromCompleteToReduced() {
    //given
    StopPointVersion stopPointVersionToUpdate = StopPointTestData.builderVersionCompleteFull().build();
    stopPointRepository.saveAndFlush(stopPointVersionToUpdate);
    StopPointVersion stopPointVersionToUpdate1 = StopPointTestData.builderVersionCompleteFull()
        .validFrom(LocalDate.of(2004, 1, 1))
        .validTo(LocalDate.of(2004, 12, 31))
        .address("napoli")
        .build();
    stopPointRepository.saveAndFlush(stopPointVersionToUpdate1);

    StopPointVersion stopPointVersionToUpdate2 = StopPointTestData.builderVersionCompleteFull()
        .validFrom(LocalDate.of(2005, 1, 1))
        .validTo(LocalDate.of(2005, 12, 31))
        .address("roma")
        .build();
    stopPointRepository.saveAndFlush(stopPointVersionToUpdate2);

    //Create Reference Point
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    String parentServicePointSloid = stopPointVersionToUpdate.getSloid();
    referencePointVersion.setParentServicePointSloid(parentServicePointSloid);
    referencePointRepository.save(referencePointVersion);

    //Create Platforms
    PlatformVersion platformVersion1 = PlatformTestData.getCompletePlatformVersion();
    platformVersion1.setParentServicePointSloid(parentServicePointSloid);
    platformRepository.saveAndFlush(platformVersion1);

    PlatformVersion platformVersion2 = PlatformTestData.getCompletePlatformVersion();
    platformVersion2.setValidFrom(LocalDate.of(2001, 1, 1));
    platformVersion2.setValidTo(LocalDate.of(2001, 12, 31));
    platformVersion2.setInclination(777.77);
    platformVersion2.setParentServicePointSloid(parentServicePointSloid);
    platformRepository.saveAndFlush(platformVersion2);

    PlatformVersion platformVersion3 = PlatformTestData.getCompletePlatformVersion();
    platformVersion3.setValidFrom(LocalDate.of(2002, 1, 1));
    platformVersion3.setValidTo(LocalDate.of(2002, 12, 31));
    platformVersion3.setInclination(666.77);
    platformVersion3.setParentServicePointSloid(parentServicePointSloid);
    platformRepository.saveAndFlush(platformVersion3);

    //create Toilets
    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    toiletVersion.setParentServicePointSloid(parentServicePointSloid);
    toiletRepository.saveAndFlush(toiletVersion);

    //create parkingLot
    ParkingLotVersion parkingLot = ParkingLotTestData.getParkingLotVersion();
    parkingLot.setParentServicePointSloid(parentServicePointSloid);
    parkingLotRepository.saveAndFlush(parkingLot);

    // Create ContactPoint
    ContactPointVersion contactPointVersion = ContactPointTestData.getContactPointVersion();
    contactPointVersion.setParentServicePointSloid(parentServicePointSloid);
    contactPointRepository.saveAndFlush(contactPointVersion);

    StopPointVersion stopPointVersionEdited = StopPointTestData.builderVersionCompleteFull()
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .build();
    //when
    StopPointVersion result = prmChangeRecordingVariantService.stopPointChangeRecordingVariant(stopPointVersionToUpdate,
        stopPointVersionEdited);

    //then
    assertThat(result).isNotNull();
    assertThat(result.isReduced()).isTrue();
    assertStopPointContent(stopPointVersionToUpdate, result);
    //assert Platform
    List<PlatformVersion> results = platformRepository.findByParentServicePointSloid(parentServicePointSloid);
    assertPlatformContents(platformVersion1, results);
    //assert ReferencePoint
    List<ReferencePointVersion> referencePointVersions = referencePointRepository.findByParentServicePointSloid(
        parentServicePointSloid);
    assertThat(referencePointVersions.stream().map(ReferencePointVersion::getStatus)).containsOnly(Status.REVOKED);
    assertThat(referencePointVersions.get(0)).isEqualTo(referencePointVersion);
    //assert Relations
    List<RelationVersion> relationVersions = relationRepository.findAllByParentServicePointSloid(parentServicePointSloid);
    assertThat(relationVersions).isEmpty();
    //assert Toilet
    List<ToiletVersion> toiletVersions = toiletRepository.findByParentServicePointSloid(parentServicePointSloid);
    assertThat(toiletVersions).hasSize(1);
    assertThat(toiletVersions).containsExactly(toiletVersion);
    //assert parkingLot
    List<ParkingLotVersion> parkingLotVersions = parkingLotRepository.findByParentServicePointSloid(parentServicePointSloid);
    assertThat(parkingLotVersions).hasSize(1);
    assertThat(parkingLotVersions).containsExactly(parkingLot);
    //assert contactPoint
    List<ContactPointVersion> contactPointVersions = contactPointRepository.findByParentServicePointSloid(
        parentServicePointSloid);
    assertThat(contactPointVersions).hasSize(1);
    assertThat(contactPointVersions).containsExactly(contactPointVersion);
  }

  @Test
  void shouldChangeStopPointRecordVariantFromCompleteToReduced() {
    //given
    StopPointVersion stopPointVersionToUpdate = StopPointTestData.builderVersionCompleteFull().build();
    stopPointRepository.saveAndFlush(stopPointVersionToUpdate);
    StopPointVersion stopPointVersionToUpdate1 = StopPointTestData.builderVersionCompleteFull()
        .validFrom(LocalDate.of(2004, 1, 1))
        .validTo(LocalDate.of(2004, 12, 31))
        .address("napoli")
        .build();
    stopPointRepository.saveAndFlush(stopPointVersionToUpdate1);

    StopPointVersion stopPointVersionToUpdate2 = StopPointTestData.builderVersionCompleteFull()
        .validFrom(LocalDate.of(2005, 1, 1))
        .validTo(LocalDate.of(2005, 12, 31))
        .address("roma")
        .build();
    stopPointRepository.saveAndFlush(stopPointVersionToUpdate2);

    //when
    StopPointVersion result = prmChangeRecordingVariantService.stopPointChangeRecordingVariant(stopPointVersionToUpdate,
        Set.of(MeanOfTransport.BUS));

    //then
    assertThat(result).isNotNull();
    assertThat(result.isReduced()).isTrue();
    assertStopPointContent(stopPointVersionToUpdate, result);

  }

  private static void assertStopPointContent(StopPointVersion stopPointVersionToUpdate, StopPointVersion result) {
    assertThat(result.getSloid()).isEqualTo(stopPointVersionToUpdate.getSloid());
    assertThat(result.getNumber()).isEqualTo(stopPointVersionToUpdate.getNumber());
    assertThat(result.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(result.getValidFrom()).isEqualTo(LocalDate.of(2003, 1, 1));
    assertThat(result.getValidTo()).isEqualTo(LocalDate.of(2005, 12, 31));
    assertThat(result.getAdditionalInformation()).isNull();
    assertThat(result.getFreeText()).isNull();
    assertThat(result.getAddress()).isNull();
    assertThat(result.getZipCode()).isNull();
    assertThat(result.getCity()).isNull();
    assertThat(result.getAlternativeTransportCondition()).isNull();
    assertThat(result.getAssistanceCondition()).isNull();
    assertThat(result.getInfoTicketMachine()).isNull();
    assertThat(result.getUrl()).isNull();
    assertThat(result.getMeansOfTransport()).containsOnly(MeanOfTransport.BUS);
    assertThat(result.getAlternativeTransport()).isEqualTo(StandardAttributeType.TO_BE_COMPLETED);
    assertThat(result.getAssistanceAvailability()).isEqualTo(StandardAttributeType.TO_BE_COMPLETED);
    assertThat(result.getAssistanceService()).isEqualTo(StandardAttributeType.TO_BE_COMPLETED);
    assertThat(result.getAudioTicketMachine()).isEqualTo(StandardAttributeType.TO_BE_COMPLETED);
    assertThat(result.getDynamicAudioSystem()).isEqualTo(StandardAttributeType.TO_BE_COMPLETED);
    assertThat(result.getDynamicOpticSystem()).isEqualTo(StandardAttributeType.TO_BE_COMPLETED);
    assertThat(result.getVisualInfo()).isEqualTo(StandardAttributeType.TO_BE_COMPLETED);
    assertThat(result.getWheelchairTicketMachine()).isEqualTo(StandardAttributeType.TO_BE_COMPLETED);
    assertThat(result.getAssistanceRequestFulfilled()).isEqualTo(BooleanOptionalAttributeType.TO_BE_COMPLETED);
    assertThat(result.getTicketMachine()).isEqualTo(BooleanOptionalAttributeType.TO_BE_COMPLETED);
  }

  @Test
  void shouldChangeStopPointRecordVariantFromReducedToComplete() {
    //given
    StopPointVersion stopPointVersionToUpdate =
        StopPointTestData.builderVersionCompleteFull().meansOfTransport(Set.of(MeanOfTransport.BUS)).build();
    stopPointRepository.saveAndFlush(stopPointVersionToUpdate);
    StopPointVersion stopPointVersionToUpdate1 = StopPointTestData.builderVersionCompleteFull()
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .validFrom(LocalDate.of(2004, 1, 1))
        .validTo(LocalDate.of(2004, 12, 31))
        .address("napoli")
        .build();
    stopPointRepository.saveAndFlush(stopPointVersionToUpdate1);

    StopPointVersion stopPointVersionToUpdate2 = StopPointTestData.builderVersionCompleteFull()
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .validFrom(LocalDate.of(2005, 1, 1))
        .validTo(LocalDate.of(2005, 12, 31))
        .address("roma")
        .build();
    stopPointRepository.saveAndFlush(stopPointVersionToUpdate2);

    //when
    StopPointVersion result = prmChangeRecordingVariantService.stopPointChangeRecordingVariant(stopPointVersionToUpdate,
        Set.of(MeanOfTransport.TRAIN));

    //then
    assertThat(result).isNotNull();
    assertThat(result.getSloid()).isEqualTo(stopPointVersionToUpdate.getSloid());
    assertThat(result.getNumber()).isEqualTo(stopPointVersionToUpdate.getNumber());
    assertThat(result.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(result.getValidFrom()).isEqualTo(LocalDate.of(2003, 1, 1));
    assertThat(result.getValidTo()).isEqualTo(LocalDate.of(2005, 12, 31));
    assertThat(result.isReduced()).isFalse();
    assertThat(result.getAdditionalInformation()).isNull();
    assertThat(result.getFreeText()).isNull();
    assertThat(result.getAddress()).isNull();
    assertThat(result.getZipCode()).isNull();
    assertThat(result.getCity()).isNull();
    assertThat(result.getAlternativeTransportCondition()).isNull();
    assertThat(result.getAssistanceCondition()).isNull();
    assertThat(result.getInfoTicketMachine()).isNull();
    assertThat(result.getUrl()).isNull();
    assertThat(result.getMeansOfTransport()).containsOnly(MeanOfTransport.TRAIN);
    assertThat(result.getAlternativeTransport()).isEqualTo(StandardAttributeType.TO_BE_COMPLETED);
    assertThat(result.getAssistanceAvailability()).isEqualTo(StandardAttributeType.TO_BE_COMPLETED);
    assertThat(result.getAssistanceService()).isEqualTo(StandardAttributeType.TO_BE_COMPLETED);
    assertThat(result.getAudioTicketMachine()).isEqualTo(StandardAttributeType.TO_BE_COMPLETED);
    assertThat(result.getDynamicAudioSystem()).isEqualTo(StandardAttributeType.TO_BE_COMPLETED);
    assertThat(result.getDynamicOpticSystem()).isEqualTo(StandardAttributeType.TO_BE_COMPLETED);
    assertThat(result.getVisualInfo()).isEqualTo(StandardAttributeType.TO_BE_COMPLETED);
    assertThat(result.getWheelchairTicketMachine()).isEqualTo(StandardAttributeType.TO_BE_COMPLETED);
    assertThat(result.getAssistanceRequestFulfilled()).isEqualTo(BooleanOptionalAttributeType.TO_BE_COMPLETED);
    assertThat(result.getTicketMachine()).isEqualTo(BooleanOptionalAttributeType.TO_BE_COMPLETED);

  }

  @Test
  void shouldPlatformChangeRecordingVariant() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(stopPointVersion.getParentServicePointSloid());
    referencePointRepository.save(referencePointVersion);

    PlatformVersion platformVersion1 = PlatformTestData.getCompletePlatformVersion();
    platformVersion1.setParentServicePointSloid(stopPointVersion.getParentServicePointSloid());
    platformRepository.saveAndFlush(platformVersion1);

    PlatformVersion platformVersion2 = PlatformTestData.getCompletePlatformVersion();
    platformVersion2.setValidFrom(LocalDate.of(2001, 1, 1));
    platformVersion2.setValidTo(LocalDate.of(2001, 12, 31));
    platformVersion2.setInclination(777.77);
    platformVersion2.setParentServicePointSloid(stopPointVersion.getParentServicePointSloid());
    platformRepository.saveAndFlush(platformVersion2);

    PlatformVersion platformVersion3 = PlatformTestData.getCompletePlatformVersion();
    platformVersion3.setValidFrom(LocalDate.of(2002, 1, 1));
    platformVersion3.setValidTo(LocalDate.of(2002, 12, 31));
    platformVersion3.setInclination(666.77);
    platformVersion3.setParentServicePointSloid(stopPointVersion.getParentServicePointSloid());
    platformRepository.saveAndFlush(platformVersion3);

    //when
    prmChangeRecordingVariantService.platformChangeRecordingVariant(platformVersion1.getParentServicePointSloid());

    //then
    List<PlatformVersion> results = platformRepository.findByParentServicePointSloid(
        stopPointVersion.getParentServicePointSloid());

    assertPlatformContents(platformVersion1, results);
  }

  private static void assertPlatformContents(PlatformVersion platformVersion1, List<PlatformVersion> results) {
    assertThat(results).hasSize(1);
    PlatformVersion result = results.get(0);
    assertThat(result).isNotNull();
    assertThat(result.getSloid()).isEqualTo(platformVersion1.getSloid());
    assertThat(result.getParentServicePointSloid()).isEqualTo(platformVersion1.getParentServicePointSloid());
    assertThat(result.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(result.getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(result.getValidTo()).isEqualTo(LocalDate.of(2002, 12, 31));
    assertThat(result.getAdditionalInformation()).isNull();
    assertThat(result.getContrastingAreas()).isEqualTo(BooleanOptionalAttributeType.TO_BE_COMPLETED);
    assertThat(result.getBoardingDevice()).isEqualTo(BoardingDeviceAttributeType.TO_BE_COMPLETED);
    assertThat(result.getDynamicAudio()).isEqualTo(BasicAttributeType.TO_BE_COMPLETED);
    assertThat(result.getDynamicVisual()).isEqualTo(BasicAttributeType.TO_BE_COMPLETED);
    assertThat(result.getLevelAccessWheelchair()).isEqualTo(BasicAttributeType.TO_BE_COMPLETED);
    assertThat(result.getTactileSystem()).isEqualTo(BooleanOptionalAttributeType.TO_BE_COMPLETED);
    assertThat(result.getVehicleAccess()).isEqualTo(VehicleAccessAttributeType.TO_BE_COMPLETED);
  }

  @Test
  void shouldSetStatusToRevokedToReferencePoints() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);

    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.saveAndFlush(referencePointVersion);

    ReferencePointVersion referencePointVersion1 = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion1.setAdditionalInformation("asd");
    referencePointVersion1.setValidFrom(LocalDate.of(2001, 1, 1));
    referencePointVersion1.setValidTo(LocalDate.of(2001, 1, 1));
    referencePointVersion1.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.saveAndFlush(referencePointVersion1);

    ReferencePointVersion referencePointVersion2 = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion2.setAdditionalInformation("dsa");
    referencePointVersion2.setValidFrom(LocalDate.of(2002, 1, 1));
    referencePointVersion2.setValidTo(LocalDate.of(2002, 1, 1));
    referencePointVersion2.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.saveAndFlush(referencePointVersion2);
    //when
    List<ReferencePointVersion> result =
        prmChangeRecordingVariantService.setStatusRevokedToReferencePoints(referencePointVersion.getParentServicePointSloid());

    //then
    assertThat(result.stream().map(ReferencePointVersion::getStatus)).containsOnly(Status.REVOKED);
    assertThat(result.get(0)).isEqualTo(referencePointVersion);
    assertThat(result.get(1)).isEqualTo(referencePointVersion1);
    assertThat(result.get(2)).isEqualTo(referencePointVersion2);
  }

}

