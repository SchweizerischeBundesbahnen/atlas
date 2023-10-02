package ch.sbb.prm.directory.controller;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;

class StopPlaceVersionControllerApiTest extends BaseControllerApiTest {

  @Autowired
  private StopPlaceController stopPlaceController;

  @AfterEach
  void tearDown() {
  }

}
