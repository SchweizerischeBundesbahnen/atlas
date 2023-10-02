package ch.sbb.prm.directory.repository;

import ch.sbb.atlas.model.controller.IntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
 class StopPlaceRepositoryTest {

  private final StopPlaceRepository stopPlaceRepository;

  @Autowired
  StopPlaceRepositoryTest(StopPlaceRepository stopPlaceRepository) {
    this.stopPlaceRepository = stopPlaceRepository;
  }

}