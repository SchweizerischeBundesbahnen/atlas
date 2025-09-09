package ch.sbb.atlas.servicepointdirectory.module.sectorgroup.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepointdirectory.module.sector.SectorTestData;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.entity.SectorGroupVersion;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.repository.SectorGroupVersionRepository;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.entity.TrafficPointElementVersion.Fields;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SectorGroupApiInternalControllerTest extends BaseControllerApiTest {

  private static final String BASE_PATH = "/internal/sector-groups";

  private final SectorGroupVersionRepository sectorGroupVersionRepository;

  private SectorGroupVersion sectorGroupVersion;

  @Autowired
  public SectorGroupApiInternalControllerTest(SectorGroupVersionRepository sectorGroupVersionRepository) {
    this.sectorGroupVersionRepository = sectorGroupVersionRepository;
  }

  @BeforeEach
  void createDefaultVersion() {
    sectorGroupVersion = SectorTestData.getBasicSectorGroupVersion();
    this.sectorGroupVersion = sectorGroupVersionRepository.save(sectorGroupVersion);
  }

  @AfterEach
  void cleanUpDb() {
    sectorGroupVersionRepository.deleteAll();
  }

  @Test
  void shouldGetSectorGroupsByTrafficPointSloid() throws Exception {
    mvc.perform(get(BASE_PATH + "/" + sectorGroupVersion.getTrafficPointSloid() + "/overview")).andExpect(status().isOk())
        .andExpect(jsonPath("$.objects[0]." + Fields.sloid, is("ch:1:sloid:group:1")))
        .andExpect(jsonPath("$.objects[0]." + Fields.designation, is("test")));
  }

}
