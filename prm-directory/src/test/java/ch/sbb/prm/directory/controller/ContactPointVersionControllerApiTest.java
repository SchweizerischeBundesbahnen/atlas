package ch.sbb.prm.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.client.location.LocationClient;
import ch.sbb.atlas.api.location.ClaimSloidRequestModel;
import ch.sbb.atlas.api.prm.enumeration.ContactPointType;
import ch.sbb.atlas.api.prm.model.contactpoint.ContactPointVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointVersionModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.ContactPointTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.SharedServicePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.ContactPointVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.SharedServicePoint;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.repository.ContactPointRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.service.RelationService;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ContactPointVersionControllerApiTest extends BaseControllerApiTest {

  private static final String PARENT_SERVICE_POINT_SLOID = "ch:1:sloid:7000";
  private final ContactPointRepository contactPointRepository;
  private final StopPointRepository stopPointRepository;
  private final ReferencePointRepository referencePointRepository;
  private final SharedServicePointRepository sharedServicePointRepository;

  @MockBean
  private final RelationService relationService;

  @MockBean
  private LocationClient locationClient;

  @Autowired
  ContactPointVersionControllerApiTest(ContactPointRepository contactPointRepository,
      StopPointRepository stopPointRepository,
      ReferencePointRepository referencePointRepository,
      SharedServicePointRepository sharedServicePointRepository,
      RelationService relationService) {
    this.contactPointRepository = contactPointRepository;
    this.stopPointRepository = stopPointRepository;
    this.referencePointRepository = referencePointRepository;
    this.sharedServicePointRepository = sharedServicePointRepository;
    this.relationService = relationService;
  }

  @BeforeEach
  void setUp() {
    SharedServicePoint servicePoint = SharedServicePointTestData.buildSharedServicePoint("ch:1:sloid:7000",
        Set.of("ch:1:sboid:100602"), Collections.emptySet());
    sharedServicePointRepository.saveAndFlush(servicePoint);
  }

  @AfterEach
  void cleanUp() {
    sharedServicePointRepository.deleteAll();
  }

  @Test
  void shouldGetContactPointsVersion() throws Exception {
    //given
    contactPointRepository.save(ContactPointTestData.getContactPointVersion());
    //when & then
    mvc.perform(get("/v1/contact-points"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void shouldCreateContactPoint() throws Exception {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);

    ContactPointVersionModel contactPointVersionModel = ContactPointTestData.getContactPointVersionModel();
    contactPointVersionModel.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when && then
    mvc.perform(post("/v1/contact-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(contactPointVersionModel)))
        .andExpect(status().isCreated());
    verify(relationService, times(1)).save(any(RelationVersion.class));
    verify(locationClient, times(1)).claimSloid(eq(new ClaimSloidRequestModel("ch:1.sloid:12345:1")));
  }

  @Test
  void shouldCreateContactPointWithoutRelationWhenStopPointIsReduced() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointVersion.setMeansOfTransport(Set.of(MeanOfTransport.BUS));
    stopPointRepository.save(stopPointVersion);

    ContactPointVersionModel contactPointVersionModel = ContactPointTestData.getContactPointVersionModel();
    contactPointVersionModel.setParentServicePointSloid(parentServicePointSloid);
    //when && then
    mvc.perform(post("/v1/contact-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(contactPointVersionModel)))
        .andExpect(status().isCreated());
    verify(relationService, never()).save(any(RelationVersion.class));
    verify(locationClient, times(1)).claimSloid(eq(new ClaimSloidRequestModel("ch:1.sloid:12345:1")));
  }

  @Test
  void shouldNotCreateContactPointWhenStopPointDoesExist() throws Exception {
    //given
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);

    ContactPointVersionModel contactPointVersionModel = ContactPointTestData.getContactPointVersionModel();
    contactPointVersionModel.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when && then
    mvc.perform(post("/v1/contact-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(contactPointVersionModel)))
        .andExpect(status().isPreconditionFailed())
        .andExpect(jsonPath("$.message", is("The stop point with sloid ch:1:sloid:7000 does not exist.")));
    verify(relationService, times(0)).save(any(RelationVersion.class));
    verify(locationClient, never()).claimSloid(any());
  }

  @Test
  void shouldNotCreateContactPointVersionWhenParentSloidDoesNotExist() throws Exception {
    //given
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid("ch:1:sloid:7001");
    referencePointRepository.save(referencePointVersion);

    ContactPointVersionModel contactPointVersionModel = ContactPointTestData.getContactPointVersionModel();
    contactPointVersionModel.setParentServicePointSloid("ch:1:sloid:7001");
    //when && then
    mvc.perform(post("/v1/contact-points")
                    .contentType(contentType)
                    .content(mapper.writeValueAsString(contactPointVersionModel)))
            .andExpect(status().isPreconditionFailed())
            .andExpect(jsonPath("$.message", is("The service point with sloid ch:1:sloid:7001 does not exist.")));
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
  void shouldUpdateContactPoint() throws Exception {
    // given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);

    ContactPointVersion version1 = ContactPointTestData.builderVersion1().build();
    version1.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    contactPointRepository.saveAndFlush(version1);
    ContactPointVersion version2 = ContactPointTestData.builderVersion2().build();
    version2.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    contactPointRepository.saveAndFlush(version2);

    ContactPointVersionModel editedVersionModel = new ContactPointVersionModel();
    editedVersionModel.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    editedVersionModel.setSloid(version2.getSloid());
    editedVersionModel.setValidFrom(version2.getValidFrom());
    editedVersionModel.setValidTo(version2.getValidTo().minusYears(1));
    editedVersionModel.setDesignation(version2.getDesignation());
    editedVersionModel.setAdditionalInformation(version2.getAdditionalInformation());
    editedVersionModel.setInductionLoop(version2.getInductionLoop());
    editedVersionModel.setOpeningHours(version2.getOpeningHours());
    editedVersionModel.setWheelchairAccess(version2.getWheelchairAccess());
    editedVersionModel.setType(ContactPointType.INFORMATION_DESK);
    editedVersionModel.setCreationDate(version2.getCreationDate());
    editedVersionModel.setEditionDate(version2.getEditionDate());
    editedVersionModel.setCreator(version2.getCreator());
    editedVersionModel.setEditor(version2.getEditor());
    editedVersionModel.setEtagVersion(version2.getVersion());

    //when & then
    mvc.perform(put("/v1/contact-points/" + version2.getId()).contentType(contentType)
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
