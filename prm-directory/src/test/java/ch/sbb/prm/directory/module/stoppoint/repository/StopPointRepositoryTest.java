package ch.sbb.prm.directory.module.stoppoint.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.prm.directory.module.stoppoint.StopPointTestData;
import ch.sbb.prm.directory.module.stoppoint.entity.StopPointVersion;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class StopPointRepositoryTest {

  private final StopPointRepository stopPointRepository;

  @Autowired
  StopPointRepositoryTest(StopPointRepository stopPointRepository) {
    this.stopPointRepository = stopPointRepository;
  }

  @BeforeEach()
  void initDB() {
    stopPointRepository.save(StopPointTestData.getStopPointVersion());
  }

  @Test
  void shouldReturnStopPoints() {
    //when
    List<StopPointVersion> result = stopPointRepository.findAll();
    //then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getMeansOfTransport()).hasSize(2);
  }

}