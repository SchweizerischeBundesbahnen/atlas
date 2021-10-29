package ch.sbb.line.directory.swiss.number;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.line.directory.IntegrationTest;
import ch.sbb.line.directory.swiss.number.SwissNumberTestEntity.SwissNumberTestEntityBuilder;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class SwissNumberUniqueValidatorTest {

  private final SwissNumberUniqueValidator swissNumberUniqueValidator;
  private final SwissNumberRepository repository;

  @Autowired
  public SwissNumberUniqueValidatorTest(SwissNumberUniqueValidator swissNumberUniqueValidator,
      SwissNumberRepository repository) {
    this.swissNumberUniqueValidator = swissNumberUniqueValidator;
    this.repository = repository;
  }

  /**
   * New:                  |_________1___________|
   * Current: |-----1-----|                       |-----------1---------|
   */
  @Test
  void shouldAllowSwissNumberOnDifferentSwissIds() {
    // Given
    repository.save(entityBuilder().validFrom(LocalDate.of(2019, 1, 1))
                                   .validTo(LocalDate.of(2019, 12, 31))
                                   .build());
    repository.save(entityBuilder().validFrom(LocalDate.of(2021, 1, 1))
                                   .validTo(LocalDate.of(2021, 12, 31))
                                   .build());
    // When
    assertThat(swissNumberUniqueValidator.hasUniqueBusinessIdOverTime(entity())).isTrue();

    // Then
  }

  /**
   * New:           |____1____|
   * Current:   |--------1--------|
   */
  @Test
  void shouldNotAllowSwissNumberOnOverlapBetween() {
    // Given
    repository.save(entityBuilder().validFrom(LocalDate.of(2019, 1, 1))
                                   .validTo(LocalDate.of(2099, 12, 31))
                                   .build());
    // When
    assertThat(swissNumberUniqueValidator.hasUniqueBusinessIdOverTime(entity())).isFalse();

    // Then
  }

  /**
   * New:         |____1____|
   * Current:         |--------1--------|
   */
  @Test
  void shouldNotAllowSwissNumberOnOverlapBeginning() {
    // Given
    repository.save(entityBuilder().validFrom(LocalDate.of(2020, 10, 1))
                                   .validTo(LocalDate.of(2099, 12, 31))
                                   .build());
    // When
    assertThat(swissNumberUniqueValidator.hasUniqueBusinessIdOverTime(entity())).isFalse();

    // Then
  }

  /**
   * New:                   |____1____|
   * Current: |--------1--------|
   */
  @Test
  void shouldNotAllowSwissNumberOnOverlapEnd() {
    // Given
    repository.save(entityBuilder().validFrom(LocalDate.of(2000, 1, 1))
                                   .validTo(LocalDate.of(2020, 10, 31))
                                   .build());
    // When
    assertThat(swissNumberUniqueValidator.hasUniqueBusinessIdOverTime(entity())).isFalse();

    // Then
  }

  /**
   * New:     |____1____|
   * Current: |----1----|
   */
  @Test
  void shouldAllowUpdateOnSameEntity() {
    // Given
    SwissNumberTestEntity entity = repository.save(entity());
    // When
    assertThat(swissNumberUniqueValidator.hasUniqueBusinessIdOverTime(entity)).isTrue();

    // Then
  }

  private SwissNumberTestEntity entity() {
    return entityBuilder().build();
  }

  private SwissNumberTestEntityBuilder entityBuilder() {
    return SwissNumberTestEntity.builder()
                                .swissLineNumber("L1")
                                .validFrom(LocalDate.of(2020, 1, 1))
                                .validTo(LocalDate.of(2020, 12, 31));

  }
}