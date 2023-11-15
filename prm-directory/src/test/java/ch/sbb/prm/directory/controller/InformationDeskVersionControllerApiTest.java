package ch.sbb.prm.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.prm.model.informationdesk.CreateInformationDeskVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointVersionModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.InformationDeskTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.InformationDeskVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.SharedServicePoint;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.repository.InformationDeskRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.service.RelationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class InformationDeskVersionControllerApiTest extends BaseControllerApiTest {

  private static final String PARENT_SERVICE_POINT_SLOID = "ch:1:sloid:7000";
  private final InformationDeskRepository informationDeskRepository;
  private final StopPointRepository stopPointRepository;
  private final ReferencePointRepository referencePointRepository;
  private final SharedServicePointRepository sharedServicePointRepository;

  @MockBean
  private final RelationService relationService;

  @Autowired
  InformationDeskVersionControllerApiTest(InformationDeskRepository informationDeskRepository,
                                          StopPointRepository stopPointRepository,
                                          ReferencePointRepository referencePointRepository,
                                          SharedServicePointRepository sharedServicePointRepository,
                                          RelationService relationService) {
    this.informationDeskRepository = informationDeskRepository;
    this.stopPointRepository = stopPointRepository;
    this.referencePointRepository = referencePointRepository;
    this.sharedServicePointRepository = sharedServicePointRepository;
    this.relationService = relationService;
  }

  @BeforeEach
  void setUp() {
    SharedServicePoint servicePoint = SharedServicePoint.builder()
            .servicePoint("{\"servicePointSloid\":\"ch:1:sloid:7000\",\"sboids\":[\"ch:1:sboid:100602\"],"
                    + "\"trafficPointSloids\":[]}")
            .sloid("ch:1:sloid:7000")
            .build();
    sharedServicePointRepository.saveAndFlush(servicePoint);
  }

  @AfterEach
  void cleanUp() {
    sharedServicePointRepository.deleteAll();
  }

  @Test
  void shouldGetInformationDesksVersion() throws Exception {
    //given
    informationDeskRepository.save(InformationDeskTestData.getInformationDeskVersion());
    //when & then
    mvc.perform(get("/v1/information-desks"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void shouldCreateInformationDesk() throws Exception {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);

    CreateInformationDeskVersionModel createInformationDeskVersionModel = InformationDeskTestData.getCreateInformationDeskVersionModel();
    createInformationDeskVersionModel.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when && then
    mvc.perform(post("/v1/information-desks")
            .contentType(contentType)
            .content(mapper.writeValueAsString(createInformationDeskVersionModel)))
        .andExpect(status().isCreated());
    verify(relationService, times(1)).save(any(RelationVersion.class));
  }

  @Test
  void shouldCreateInformationDeskWithoutRelationWhenStopPointIsReduced() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointVersion.setMeansOfTransport(Set.of(MeanOfTransport.BUS));
    stopPointRepository.save(stopPointVersion);

    CreateInformationDeskVersionModel createInformationDeskVersionModel = InformationDeskTestData.getCreateInformationDeskVersionModel();
    createInformationDeskVersionModel.setParentServicePointSloid(parentServicePointSloid);
    //when && then
    mvc.perform(post("/v1/information-desks")
            .contentType(contentType)
            .content(mapper.writeValueAsString(createInformationDeskVersionModel)))
        .andExpect(status().isCreated());
    verify(relationService, never()).save(any(RelationVersion.class));
  }

  @Test
  void shouldNotCreateInformationDeskWhenStopPointDoesExist() throws Exception {
    //given
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);

    CreateInformationDeskVersionModel createInformationDeskVersionModel = InformationDeskTestData.getCreateInformationDeskVersionModel();
    createInformationDeskVersionModel.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when && then
    mvc.perform(post("/v1/information-desks")
            .contentType(contentType)
            .content(mapper.writeValueAsString(createInformationDeskVersionModel)))
        .andExpect(status().isPreconditionFailed())
        .andExpect(jsonPath("$.message", is("The stop place with sloid ch:1:sloid:7000 does not exists.")));
    verify(relationService, times(0)).save(any(RelationVersion.class));
  }

  /**
   * Szenario 8a: Letzte Version terminieren wenn nur validTo ist updated
   * NEU:      |______________________|
   * IST:      |-------------------------------------------------------
   * Version:                            1
   *
   * RESULTAT: |----------------------| Version wird per xx aufgehoben
   * Version:         1
   */
  @Test
  void shouldUpdateInformationDesk() throws Exception {
    // given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);

    InformationDeskVersion version1 = InformationDeskTestData.builderVersion1().build();
    version1.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    informationDeskRepository.saveAndFlush(version1);
    InformationDeskVersion version2 = InformationDeskTestData.builderVersion2().build();
    version2.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    informationDeskRepository.saveAndFlush(version2);

    CreateInformationDeskVersionModel editedVersionModel = new CreateInformationDeskVersionModel();
    editedVersionModel.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    editedVersionModel.setSloid(version2.getSloid());
    editedVersionModel.setValidFrom(version2.getValidFrom());
    editedVersionModel.setValidTo(version2.getValidTo().minusYears(1));
    editedVersionModel.setNumberWithoutCheckDigit(version2.getNumber().getNumber());
    editedVersionModel.setDesignation(version2.getDesignation());
    editedVersionModel.setAdditionalInformation(version2.getAdditionalInformation());
    editedVersionModel.setInductionLoop(version2.getInductionLoop());
    editedVersionModel.setOpeningHours(version2.getOpeningHours());
    editedVersionModel.setWheelchairAccess(version2.getWheelchairAccess());
    editedVersionModel.setCreationDate(version2.getCreationDate());
    editedVersionModel.setEditionDate(version2.getEditionDate());
    editedVersionModel.setCreator(version2.getCreator());
    editedVersionModel.setEditor(version2.getEditor());
    editedVersionModel.setEtagVersion(version2.getVersion());

    //when & then
    mvc.perform(put("/v1/information-desks/" + version2.getId()).contentType(contentType)
            .content(mapper.writeValueAsString(editedVersionModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2000-01-01")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2000-12-31")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2001-01-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2001-12-31")));
  }

  @Test
  void shouldNotCreateInformationDeskVersionWhenParentSloidDoesNotExist() throws Exception {
    //given
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid("ch:1:sloid:7001");
    referencePointRepository.save(referencePointVersion);

    CreateInformationDeskVersionModel createInformationDeskVersionModel = InformationDeskTestData.getCreateInformationDeskVersionModel();
    createInformationDeskVersionModel.setParentServicePointSloid("ch:1:sloid:7001");
    //when && then
    mvc.perform(post("/v1/information-desks")
                    .contentType(contentType)
                    .content(mapper.writeValueAsString(createInformationDeskVersionModel)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", is("Entity not found")));
  }

}
