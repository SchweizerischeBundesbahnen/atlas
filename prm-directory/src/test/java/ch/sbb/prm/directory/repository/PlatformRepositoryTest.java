package ch.sbb.prm.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.prm.directory.PlatformTestData;
import ch.sbb.prm.directory.entity.PlatformVersion;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class PlatformRepositoryTest {

  private final PlatformRepository platformRepository;

  @Autowired
  PlatformRepositoryTest(PlatformRepository platformRepository) {
    this.platformRepository = platformRepository;
  }

  @BeforeEach()
  void initDB() {
    platformRepository.save(PlatformTestData.getPlatformVersion());
  }

  @Test
  void shouldReturnPlatforms() {
    //when
   List<PlatformVersion> result = platformRepository.findAll();
   //then
   assertThat(result).hasSize(1);
  }

}