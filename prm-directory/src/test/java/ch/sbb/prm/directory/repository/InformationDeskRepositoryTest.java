package ch.sbb.prm.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.prm.directory.InformationDeskTestData;
import ch.sbb.prm.directory.entity.InformationDeskVersion;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class InformationDeskRepositoryTest {

  private final InformationDeskRepository informationDeskRepository;

  @Autowired
  InformationDeskRepositoryTest(InformationDeskRepository informationDeskRepository) {
    this.informationDeskRepository = informationDeskRepository;
  }

  @BeforeEach()
  void initDB() {
    informationDeskRepository.save(InformationDeskTestData.getInformationDeskVersion());
  }

  @Test
  void shouldReturnInformationDesks() {
    //when
   List<InformationDeskVersion> result = informationDeskRepository.findAll();
   //then
   assertThat(result).hasSize(1);
  }

}