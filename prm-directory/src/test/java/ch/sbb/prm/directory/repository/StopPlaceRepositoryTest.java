package ch.sbb.prm.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.prm.directory.StopPlaceTestData;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

  @BeforeEach()
  void initDB() {
    stopPlaceRepository.save(StopPlaceTestData.getStopPlaceVersion());
  }

  @Test
  void shouldReturnStopPlaces() {
    //when
   List<StopPlaceVersion> result = stopPlaceRepository.findAll();
   //then
   assertThat(result).hasSize(1);
   assertThat(result.get(0).getMeansOfTransport()).hasSize(2);
  }

}