package ch.sbb.prm.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.prm.directory.ParkingLotTestData;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ParkingLotVersionControllerApiTest extends BaseControllerApiTest {

  private final ParkingLotRepository parkingLotRepository;

  @Autowired
  ParkingLotVersionControllerApiTest(ParkingLotRepository parkingLotRepository){
    this.parkingLotRepository = parkingLotRepository;
  }

  @BeforeEach()
  void initDB() {
    parkingLotRepository.save(ParkingLotTestData.getParkingLotVersion());
  }

  @Test
  void shouldGetPlatformsVersion() throws Exception {
    mvc.perform(get("/v1/parking-lots"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

}
