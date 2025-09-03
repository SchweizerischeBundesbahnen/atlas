package ch.sbb.atlas.servicepointdirectory.module.sector.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepointdirectory.module.sector.SectorTestData;
import ch.sbb.atlas.servicepointdirectory.module.sector.entity.SectorVersion;
import ch.sbb.atlas.servicepointdirectory.module.sector.repository.SectorVersionRepository;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.entity.TrafficPointElementVersion.Fields;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SectorApiInternalControllerTest extends BaseControllerApiTest {

  private static final String BASE_PATH = "/internal/sectors";

  private final SectorVersionRepository sectorVersionRepository;

  private SectorVersion sectorVersion;

  @Autowired
  public SectorApiInternalControllerTest(SectorVersionRepository sectorVersionRepository) {
    this.sectorVersionRepository = sectorVersionRepository;
  }

  @BeforeEach
  void createDefaultVersion() {
    sectorVersion = SectorTestData.getNewBasicSectorVersion();
    this.sectorVersion = sectorVersionRepository.save(sectorVersion);
  }

  @AfterEach
  void cleanUpDb() {
    sectorVersionRepository.deleteAll();
  }

  @Test
  void shouldGetSectorsByTrafficPointSloid() throws Exception {
    mvc.perform(get(BASE_PATH + "/" + sectorVersion.getTrafficPointSloid() + "/overview")).andExpect(status().isOk())
        .andExpect(jsonPath("$.objects[0]." + Fields.sloid, is("ch:1:sloid:sector:1111")))
        .andExpect(jsonPath("$.objects[0]." + Fields.designation, is("test")));
  }

}
