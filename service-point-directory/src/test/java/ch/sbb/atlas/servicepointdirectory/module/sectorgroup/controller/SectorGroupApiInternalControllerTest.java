package ch.sbb.atlas.servicepointdirectory.module.sectorgroup.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepointdirectory.module.sector.SectorTestData;
import ch.sbb.atlas.servicepointdirectory.module.sector.repository.SectorVersionRepository;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.entity.SectorGroupRelation;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.entity.SectorGroupVersion;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.model.SectorGroupRelationId;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.repository.SectorGroupRelationRepository;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.repository.SectorGroupVersionRepository;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.entity.TrafficPointElementVersion.Fields;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.repository.TrafficPointElementVersionRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SectorGroupApiInternalControllerTest extends BaseControllerApiTest {

  private static final String BASE_PATH = "/internal/sector-groups";

  private final SectorGroupVersionRepository sectorGroupVersionRepository;
  private final SectorVersionRepository sectorVersionRepository;
  private final SectorGroupRelationRepository sectorGroupRelationRepository;
  private final TrafficPointElementVersionRepository trafficPointElementVersionRepository;

  private SectorGroupVersion sectorGroupVersion;

  @Autowired
  public SectorGroupApiInternalControllerTest(SectorGroupVersionRepository sectorGroupVersionRepository,
      SectorVersionRepository sectorVersionRepository, SectorGroupRelationRepository sectorGroupRelationRepository,
      TrafficPointElementVersionRepository trafficPointElementVersionRepository) {
    this.sectorGroupVersionRepository = sectorGroupVersionRepository;
    this.sectorVersionRepository = sectorVersionRepository;
    this.sectorGroupRelationRepository = sectorGroupRelationRepository;
    this.trafficPointElementVersionRepository = trafficPointElementVersionRepository;
  }

  @BeforeEach
  void createDefaultVersion() {
    sectorGroupVersion = SectorTestData.getBasicSectorGroupVersion();
    this.sectorGroupVersion = sectorGroupVersionRepository.save(sectorGroupVersion);
  }

  @AfterEach
  void cleanUpDb() {
    sectorGroupVersionRepository.deleteAll();
    sectorVersionRepository.deleteAll();
    sectorGroupRelationRepository.deleteAll();
    trafficPointElementVersionRepository.deleteAll();
  }

  @Test
  void shouldGetSectorGroupsByTrafficPointSloid() throws Exception {
    mvc.perform(get(BASE_PATH + "/" + sectorGroupVersion.getTrafficPointSloid() + "/overview")).andExpect(status().isOk())
        .andExpect(jsonPath("$.objects[0]." + Fields.sloid, is("ch:1:sloid:group:1")))
        .andExpect(jsonPath("$.objects[0]." + Fields.designation, is("test")));
  }

  @Test
  void shouldGetSectorBySectorGroupId() throws Exception {
    sectorGroupVersionRepository.deleteAll();
    TrafficPointElementVersion trafficPointElementVersion = trafficPointElementVersionRepository.save(
        TrafficPointTestData.getBasicTrafficPoint());

    String sector1 = "sector:1:abc";
    String sector2 = "sector:2:abc";

    sectorVersionRepository.save(SectorTestData.getBasicSectorVersion().toBuilder()
        .sloid(sector1)
        .trafficPointSloid(trafficPointElementVersion.getSloid())
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2030, 1, 1))
        .designation("dese")
        .build());

    sectorVersionRepository.save(SectorTestData.getBasicSectorVersion().toBuilder()
        .sloid(sector2)
        .trafficPointSloid(trafficPointElementVersion.getSloid())
        .validFrom(LocalDate.of(2022, 2, 1))
        .validTo(LocalDate.of(2024, 1, 1))
        .designation("hehe")
        .build());

    SectorGroupVersion sectorGroupVersion = sectorGroupVersionRepository.save(SectorTestData.getBasicSectorGroupVersion());

    sectorGroupRelationRepository.save(new SectorGroupRelation(
        new SectorGroupRelationId(sectorGroupVersion.getSloid(), sector1)));
    sectorGroupRelationRepository.save(new SectorGroupRelation(
        new SectorGroupRelationId(sectorGroupVersion.getSloid(), sector2)));

    mvc.perform(get(BASE_PATH + "/" + sectorGroupVersion.getSloid() + "/sectors"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].sloid", is(sector1)))
        .andExpect(jsonPath("$[0].designation", is("dese")))
        .andExpect(jsonPath("$[1].sloid", is(sector2)))
        .andExpect(jsonPath("$[1].designation", is("hehe")));
  }

}
