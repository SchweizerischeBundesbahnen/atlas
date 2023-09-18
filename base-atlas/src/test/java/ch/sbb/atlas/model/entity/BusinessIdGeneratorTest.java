package ch.sbb.atlas.model.entity;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@IntegrationTest
class BusinessIdGeneratorTest {

  private final DummyRepository repository;

  @Autowired
  public BusinessIdGeneratorTest(DummyRepository repository) {
    this.repository = repository;
  }

  @Sql("/business_id_generator_test.sql")
  @Test
  void shouldGenerateBusinessId() {
    // Given

    // When
    DummyEntity entity = repository.saveAndFlush(DummyEntity.builder().id(1L).build());

    // Then
    assertThat(entity.getSboid()).startsWith("ch:1:sboid:");
    assertThat(entity.getSboid()).isEqualTo("ch:1:sboid:1000000");
  }

  @Test
  void shouldNotGenerateBusinessIdWhenAlreadyGiven() {
    // Given

    // When
    DummyEntity entity = repository.saveAndFlush(
        DummyEntity.builder().id(2L).sboid("sboid").build());

    // Then
    assertThat(entity.getSboid()).isEqualTo("sboid");
  }

}
