package ch.sbb.prm.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.prm.directory.ToiletTestData;
import ch.sbb.prm.directory.entity.ToiletVersion;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class ToiletRepositoryTest {

  private final ToiletRepository toiletRepository;

  @Autowired
  ToiletRepositoryTest(ToiletRepository toiletRepository) {
    this.toiletRepository = toiletRepository;
  }

  @BeforeEach()
  void initDB() {
    toiletRepository.save(ToiletTestData.getToiletVersion());
  }

  @Test
  void shouldReturnToilets() {
    //when
   List<ToiletVersion> result = toiletRepository.findAll();
   //then
   assertThat(result).hasSize(1);
  }

}