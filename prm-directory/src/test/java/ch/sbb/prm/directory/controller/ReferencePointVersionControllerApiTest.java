package ch.sbb.prm.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.client.location.LocationClient;
import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.prm.model.referencepoint.ReferencePointVersionModel;
import ch.sbb.atlas.api.client.location.LocationClient;
import ch.sbb.atlas.api.location.ClaimSloidRequestModel;
import ch.sbb.atlas.api.prm.model.referencepoint.ReferencePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointVersionModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.ContactPointTestData;
import ch.sbb.prm.directory.ParkingLotTestData;
import ch.sbb.prm.directory.PlatformTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.SharedServicePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.ToiletTestData;
import ch.sbb.prm.directory.entity.ContactPointVersion;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.SharedServicePoint;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.repository.ContactPointRepository;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.repository.ToiletRepository;
import ch.sbb.prm.directory.service.RelationService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ReferencePointVersionControllerApiTest extends BaseControllerApiTest {

  private static final String PARENT_SERVICE_POINT_SLOID = "ch:1:sloid:7000";
  private final ReferencePointRepository referencePointRepository;
  private final StopPointRepository stopPointRepository;
  private final ContactPointRepository contactPointRepository;
  private final ParkingLotRepository parkingLotRepository;
  private final ToiletRepository toiletRepository;
  private final SharedServicePointRepository sharedServicePointRepository;
  private final PlatformRepository platformRepository;

  @MockBean
  private final RelationService relationService;

  @MockBean
  private LocationClient locationClient;

  @Autowired
  ReferencePointVersionControllerApiTest(ReferencePointRepository referencePointRepository,
      StopPointRepository stopPointRepository,
      ContactPointRepository contactPointRepository,
      ParkingLotRepository parkingLotRepository,
      ToiletRepository toiletRepository,
      SharedServicePointRepository sharedServicePointRepository,
      PlatformRepository platformRepository,
      RelationService relationService) {
    this.referencePointRepository = referencePointRepository;
    this.stopPointRepository = stopPointRepository;
    this.contactPointRepository = contactPointRepository;
    this.parkingLotRepository = parkingLotRepository;
    this.toiletRepository = toiletRepository;
    this.sharedServicePointRepository = sharedServicePointRepository;
    this.platformRepository = platformRepository;
    this.relationService = relationService;
  }

  @BeforeEach
  void setUp() {
    SharedServicePoint servicePoint = SharedServicePointTestData.buildSharedServicePoint("ch:1:sloid:7000", Set.of("ch:1:sboid:100602"),
            Collections.emptySet());
    sharedServicePointRepository.saveAndFlush(servicePoint);
  }

  @AfterEach
  void cleanUp() {
    sharedServicePointRepository.deleteAll();
  }

  @Test
  void shouldGetReferencePointsVersion() throws Exception {
    //given
    referencePointRepository.save(ReferencePointTestData.getReferencePointVersion());
    //when & then
    mvc.perform(get("/v1/reference-points"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.objects", hasSize(1)));
  }

  @Test
  void shouldGetReferencePointBySloid() throws Exception {
    //given
    ReferencePointVersion referencePointVersion = referencePointRepository.save(
        ReferencePointTestData.getReferencePointVersion());
    //when & then
    mvc.perform(get("/v1/reference-points/" + referencePointVersion.getSloid()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void shouldGetReferencePointOverview() throws Exception {
    //given
    ReferencePointVersion referencePointVersion = referencePointRepository.save(
        ReferencePointTestData.getReferencePointVersion());

    //when & then
    mvc.perform(get("/v1/reference-points/overview/" + referencePointVersion.getParentServicePointSloid()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.objects", hasSize(1)));
  }

  @Test
  void shouldCreateReferencePoint() throws Exception {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersionModel referencePointVersionModel = ReferencePointTestData.getReferencePointVersionModel();
    referencePointVersionModel.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);

    //Init PRM Relations
    ContactPointVersion contactPointVersion = ContactPointTestData.getContactPointVersion();
    contactPointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    contactPointRepository.save(contactPointVersion);
    ParkingLotVersion parkingLotVersion = ParkingLotTestData.getParkingLotVersion();
    parkingLotVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    parkingLotRepository.save(parkingLotVersion);
    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    toiletVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    toiletRepository.save(toiletVersion);
    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    platformVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    platformRepository.save(platformVersion);

    //when && then
    mvc.perform(post("/v1/reference-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(referencePointVersionModel)))
        .andExpect(status().isCreated());
    //verify that the reference point create 4 relation
    verify(relationService, times(4)).save(any(RelationVersion.class));
    verify(locationClient, times(1)).claimSloid(argThat(
        claimSloidRequestModel -> claimSloidRequestModel.sloidType() == SloidType.REFERENCE_POINT
            && Objects.equals(claimSloidRequestModel.sloid(), "ch:1:sloid:12345:1")));
  }

  @Test
  void shouldCreateReferencePointWithAutomaticSloid() throws Exception {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);

    ReferencePointVersionModel referencePointVersionModel = ReferencePointTestData.getReferencePointVersionModel();
    referencePointVersionModel.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    // Automatic sloid generation
    referencePointVersionModel.setSloid(null);

    //Init PRM Relations
    ContactPointVersion contactPointVersion = ContactPointTestData.getContactPointVersion();
    contactPointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    contactPointRepository.save(contactPointVersion);
    ParkingLotVersion parkingLotVersion = ParkingLotTestData.getParkingLotVersion();
    parkingLotVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    parkingLotRepository.save(parkingLotVersion);
    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    toiletVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    toiletRepository.save(toiletVersion);
    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    platformVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    platformRepository.save(platformVersion);

    //when && then
    mvc.perform(post("/v1/reference-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(referencePointVersionModel)))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldNotCreateReferencePointWhenStopPointIsReduced() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointVersion.setMeansOfTransport(Set.of(MeanOfTransport.BUS));
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersionModel referencePointVersionModel = ReferencePointTestData.getReferencePointVersionModel();
    referencePointVersionModel.setParentServicePointSloid(parentServicePointSloid);

    //when && then
    mvc.perform(post("/v1/reference-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(referencePointVersionModel)))
        .andExpect(status().isPreconditionFailed());
    verify(relationService, times(0)).save(any(RelationVersion.class));
    verify(locationClient, never()).claimSloid(any());
  }

  @Test
  void shouldNotCreateReferencePointWhenStopPointDoesNotExist() throws Exception {
    //given
    ReferencePointVersionModel referencePointVersionModel = ReferencePointTestData.getReferencePointVersionModel();
    referencePointVersionModel.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);

    //Init PRM Relations
    ContactPointVersion contactPointVersion = ContactPointTestData.getContactPointVersion();
    contactPointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    contactPointRepository.save(contactPointVersion);
    ParkingLotVersion parkingLotVersion = ParkingLotTestData.getParkingLotVersion();
    parkingLotVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    parkingLotRepository.save(parkingLotVersion);
    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    toiletVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    toiletRepository.save(toiletVersion);
    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    platformVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    platformRepository.save(platformVersion);

    //when && then
    mvc.perform(post("/v1/reference-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(referencePointVersionModel)))
        .andExpect(status().isPreconditionFailed())
        .andExpect(jsonPath("$.message", is("The stop point with sloid ch:1:sloid:7000 does not exist.")));
    verify(relationService, times(0)).save(any(RelationVersion.class));
    verify(locationClient, never()).claimSloid(any());
  }

  /**
   * Szenario 8a: Letzte Version terminieren wenn nur validTo ist updated
   * NEU:      |______________________|
   * IST:      |-------------------------------------------------------
   * Version:                            1
   * <p>
   * RESULTAT: |----------------------| Version wird per xx aufgehoben
   * Version:         1
   */
  @Test
  void shouldUpdateReferencePoint() throws Exception {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);

    ReferencePointVersion version1 = ReferencePointTestData.builderVersion1().build();
    version1.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.saveAndFlush(version1);

    ReferencePointVersion version2 = ReferencePointTestData.builderVersion2().build();
    version2.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.saveAndFlush(version2);

    ReferencePointVersionModel editedVersionModel = new ReferencePointVersionModel();
    editedVersionModel.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    editedVersionModel.setMainReferencePoint(version2.getMainReferencePoint());
    editedVersionModel.setReferencePointType(version2.getReferencePointType());
    editedVersionModel.setDesignation(version2.getDesignation());
    editedVersionModel.setAdditionalInformation(version2.getAdditionalInformation());
    editedVersionModel.setValidFrom(version2.getValidFrom());
    editedVersionModel.setValidTo(LocalDate.of(2001, 12, 31));
    editedVersionModel.setCreationDate(version2.getCreationDate());
    editedVersionModel.setEditionDate(version2.getEditionDate());
    editedVersionModel.setCreator(version2.getCreator());
    editedVersionModel.setEditor(version2.getEditor());
    editedVersionModel.setEtagVersion(version2.getVersion());

    //when & then
    mvc.perform(put("/v1/reference-points/" + version2.getId()).contentType(contentType)
            .content(mapper.writeValueAsString(editedVersionModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2000-01-01")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2000-12-31")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2001-01-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2001-12-31")));
    verify(locationClient, never()).claimSloid(any());
    verify(locationClient, never()).generateSloid(any());
  }

}
