package ch.sbb.prm.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.prm.directory.ContactPointTestData;
import ch.sbb.prm.directory.entity.ContactPointVersion;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class ContactPointRepositoryTest {

  private final ContactPointRepository contactPointRepository;

  @Autowired
  ContactPointRepositoryTest(ContactPointRepository contactPointRepository) {
    this.contactPointRepository = contactPointRepository;
  }

  @BeforeEach()
  void initDB() {
    contactPointRepository.save(ContactPointTestData.getContactPointVersion());
  }

  @Test
  void shouldReturnContactPoints() {
    //when
   List<ContactPointVersion> result = contactPointRepository.findAll();
   //then
   assertThat(result).hasSize(1);
  }

}