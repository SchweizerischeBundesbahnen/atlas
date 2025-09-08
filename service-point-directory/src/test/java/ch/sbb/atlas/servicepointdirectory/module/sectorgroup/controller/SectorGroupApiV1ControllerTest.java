package ch.sbb.atlas.servicepointdirectory.module.sectorgroup.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.servicepoint.sector.CreateSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.ReadSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.SectorGroupVersionModel;
import ch.sbb.atlas.location.LocationService;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.module.sector.SectorTestData;
import ch.sbb.atlas.servicepointdirectory.module.sector.entity.SectorVersion;
import ch.sbb.atlas.servicepointdirectory.module.sector.repository.SectorVersionRepository;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.entity.SectorGroupRelation;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.entity.SectorGroupVersion;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.model.SectorGroupRelationId;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.repository.SectorGroupRelationRepository;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.repository.SectorGroupVersionRepository;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.repository.TrafficPointElementVersionRepository;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

class SectorGroupApiV1ControllerTest extends BaseControllerApiTest {

  private final SectorGroupRelationRepository sectorGroupRelationRepository;
  private final SectorVersionRepository sectorVersionRepository;
  private final SectorGroupVersionRepository sectorGroupVersionRepository;
  private final TrafficPointElementVersionRepository trafficPointRepository;
  private final ServicePointVersionRepository servicePointVersionRepository;

  @MockitoBean
  private LocationService locationService;

  @Autowired
  public SectorGroupApiV1ControllerTest(SectorVersionRepository sectorVersionRepository,
      SectorGroupRelationRepository sectorGroupRelationRepository, SectorGroupVersionRepository sectorGroupVersionRepository,
      TrafficPointElementVersionRepository trafficPointRepository, ServicePointVersionRepository servicePointVersionRepository) {
    this.sectorVersionRepository = sectorVersionRepository;
    this.sectorGroupRelationRepository = sectorGroupRelationRepository;
    this.sectorGroupVersionRepository = sectorGroupVersionRepository;
    this.trafficPointRepository = trafficPointRepository;
    this.servicePointVersionRepository = servicePointVersionRepository;
  }

  @BeforeEach
  void setUp() {
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getBasicTrafficPoint();
    trafficPointElementVersion.setServicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8507000));
    
    trafficPointRepository.saveAndFlush(trafficPointElementVersion);
    servicePointVersionRepository.saveAndFlush(ServicePointTestData.getBern());
  }

  @AfterEach
  void cleanDb() {
    sectorVersionRepository.deleteAll();
    sectorGroupRelationRepository.deleteAll();
    sectorGroupVersionRepository.deleteAll();
    trafficPointRepository.deleteAll();
    servicePointVersionRepository.deleteAll();
  }

  @Test
  void shouldGetSectorGroups() throws Exception {
    sectorGroupVersionRepository.deleteAll();
    SectorGroupVersion groupVersion1 = SectorTestData.getBasicSectorGroupVersion();

    sectorGroupVersionRepository.save(groupVersion1);

    SectorGroupVersion groupVersion2 = SectorTestData.getBasicSectorGroupVersion();
    groupVersion2.setSloid("new:sloid");
    sectorGroupVersionRepository.save(groupVersion2);

    mvc.perform(get("/v1/sector-groups"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(groupVersion1.getId().intValue())))
        .andExpect(jsonPath("$[0].sloid", is(groupVersion1.getSloid())))
        .andExpect(jsonPath("$[1].id", is(groupVersion2.getId().intValue())))
        .andExpect(jsonPath("$[1].sloid", is(groupVersion2.getSloid())));
  }

  @Test
  void shouldGetSectorGroupBySloid() throws Exception {
    sectorGroupVersionRepository.deleteAll();

    String sloid = "ch:1:sloid:group:1";
    SectorGroupVersion groupVersion1 = SectorTestData.getBasicSectorGroupVersion();
    groupVersion1.setSloid(sloid);
    groupVersion1.setVersion(1);

    sectorGroupVersionRepository.save(groupVersion1);

    SectorGroupVersion groupVersion2 = SectorTestData.getBasicSectorGroupVersion();
    groupVersion2.setValidFrom(LocalDate.of(2056, 1, 1));
    groupVersion2.setValidTo(LocalDate.of(2056, 1, 2));
    groupVersion2.setSloid(sloid);
    groupVersion2.setVersion(2);

    sectorGroupVersionRepository.save(groupVersion2);

    mvc.perform(get("/v1/sector-groups/{sloid}", sloid))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].etagVersion", is(1)))
        .andExpect(jsonPath("$[1].etagVersion", is(2)));
  }

  @Test
  void shouldGetSectorGroupVersionById() throws Exception {
    sectorGroupVersionRepository.deleteAll();

    SectorGroupVersion sectorGroupVersion = sectorGroupVersionRepository.save(SectorTestData.getBasicSectorGroupVersion());

    mvc.perform(get("/v1/sector-groups/versions/{id}", sectorGroupVersion.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(sectorGroupVersion.getId().intValue())))
        .andExpect(jsonPath("$.sloid", is(sectorGroupVersion.getSloid())))
        .andExpect(jsonPath("$.sectorVersions", hasSize(0)));
  }

  @Test
  void shouldCreateSectorGroup() throws Exception {
    sectorGroupVersionRepository.deleteAll();

    SectorVersion sectorVersion = SectorTestData.getBasicSectorVersion();
    sectorVersionRepository.save(sectorVersion);

    SectorVersion sectorVersion1 = SectorTestData.getBasicSectorVersion();
    sectorVersion1.setSloid("new:sloid");
    sectorVersionRepository.save(sectorVersion1);

    CreateSectorGroupVersionModel create = CreateSectorGroupVersionModel.builder()
        .trafficPointSloid(sectorVersion.getTrafficPointSloid())
        .validFrom(LocalDate.of(2023, 1, 1))
        .validTo(LocalDate.of(2023, 12, 31))
        .designation("hihi")
        .length(17.00)
        .sectorSloids(Set.of("ch:1:sloid:sector:1", "new:sloid"))
        .build();

    doReturn("ch:1:sloid:sector:1:0:1").when(locationService).generateSloid(SloidType.SECTOR_GROUP,
        create.getTrafficPointSloid());

    MvcResult mvcResult = mvc.perform(post("/v1/sector-groups")
            .contentType(contentType)
            .content(mapper.writeValueAsString(create)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.length", is(36.0)))
        .andExpect(jsonPath("$.designation", is("hihi")))
        .andExpect(jsonPath("$.sloid", is("ch:1:sloid:sector:1:0:1")))
        .andReturn();

    String responseContent = mvcResult.getResponse().getContentAsString();
    ReadSectorGroupVersionModel createdSectorGroup = mapper.readValue(responseContent, ReadSectorGroupVersionModel.class);

    Set<SectorGroupRelation> relations = sectorGroupRelationRepository.findBySectorGroupRelationIdSectorGroupSloid(
        createdSectorGroup.getSloid());
    assertThat(relations).hasSize(2);
    assertThat(createdSectorGroup.getSloid()).isEqualTo("ch:1:sloid:sector:1:0:1");
  }

  @Test
  void shouldThrowExceptionOnCreateWhenIdNotNull() throws Exception {
    SectorVersion sectorVersion = SectorTestData.getBasicSectorVersion();
    sectorVersionRepository.save(sectorVersion);

    SectorVersion sectorVersion1 = SectorTestData.getBasicSectorVersion();
    sectorVersion1.setSloid("new:sloid");
    sectorVersionRepository.save(sectorVersion1);

    CreateSectorGroupVersionModel create = CreateSectorGroupVersionModel.builder()
        .id(1111L)
        .trafficPointSloid(sectorVersion.getTrafficPointSloid())
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2030, 1, 1))
        .designation("hihi")
        .length(17.00)
        .sectorSloids(Set.of("ch:1:sloid:sector:1", "new:sloid"))
        .build();

    doReturn("ch:1:sloid:sector:1:0:1").when(locationService).generateSloid(SloidType.SECTOR_GROUP,
        create.getTrafficPointSloid());

    mvc.perform(post("/v1/sector-groups")
            .contentType(contentType)
            .content(mapper.writeValueAsString(create)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.error", is("Constraint violation")))
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.CONSTRAINT_VIOLATION.CREATE_ID_CHECK")))
        .andExpect(jsonPath("$.details[0].message", is("ID must be null when creating a new element")));
  }

  @Test
  void shouldUpdateSectorGroupAndReturnTwoVersions() throws Exception {
    sectorGroupVersionRepository.deleteAll();

    SectorVersion sectorVersion = SectorTestData.getBasicSectorVersion();
    sectorVersionRepository.save(sectorVersion);

    SectorVersion sectorVersion1 = SectorTestData.getBasicSectorVersion();
    sectorVersion1.setSloid("new:sloid");
    sectorVersionRepository.save(sectorVersion1);

    SectorGroupVersion sectorGroupVersion = SectorTestData.getBasicSectorGroupVersion();

    sectorGroupVersion = sectorGroupVersionRepository.saveAndFlush(sectorGroupVersion);

    SectorGroupRelationId sectorGroupRelationId = SectorGroupRelationId.builder()
        .sectorGroupSloid(sectorGroupVersion.getSloid())
        .sectorSloid(sectorVersion.getSloid())
        .build();

    SectorGroupRelationId sectorGroupRelationId2 = SectorGroupRelationId.builder()
        .sectorGroupSloid(sectorGroupVersion.getSloid())
        .sectorSloid(sectorVersion1.getSloid())
        .build();

    sectorGroupRelationRepository.saveAndFlush(
        SectorGroupRelation.builder().sectorGroupRelationId(sectorGroupRelationId).build());
    sectorGroupRelationRepository.saveAndFlush(
        SectorGroupRelation.builder().sectorGroupRelationId(sectorGroupRelationId2).build());

    SectorGroupVersionModel updateDto = SectorGroupVersionModel.builder()
        .etagVersion(sectorGroupVersion.getVersion())
        .sloid(sectorVersion.getSloid())
        .trafficPointSloid(sectorVersion.getTrafficPointSloid())
        .validFrom(sectorGroupVersion.getValidFrom().plusYears(1))
        .validTo(sectorGroupVersion.getValidTo())
        .designation("novo")
        .length(sectorGroupVersion.getLength() + 1)
        .build();

    mvc.perform(put("/v1/sector-groups/{id}", sectorGroupVersion.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is((sectorGroupVersion.getId().intValue()))))
        .andExpect(jsonPath("$[0].etagVersion", is(sectorGroupVersion.getVersion() + 2)))
        .andExpect(jsonPath("$[1].etagVersion", is(sectorGroupVersion.getVersion() + 1)));
  }
}
