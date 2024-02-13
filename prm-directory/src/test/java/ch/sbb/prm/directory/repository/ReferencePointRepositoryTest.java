package ch.sbb.prm.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class ReferencePointRepositoryTest {

  private final ReferencePointRepository referencePointRepository;

  @Autowired
  ReferencePointRepositoryTest(ReferencePointRepository referencePointRepository) {
    this.referencePointRepository = referencePointRepository;
  }

  @BeforeEach()
  void initDB() {
    referencePointRepository.save(ReferencePointTestData.getReferencePointVersion());
  }

  @Test
  void shouldReturnToilets() {
    //when
   List<ReferencePointVersion> result = referencePointRepository.findAll();
   //then
   assertThat(result).hasSize(1);
  }

}