package ch.sbb.atlas.servicepointdirectory.module.servicepoint.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointGlobalId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
class ServicePointGlobalIdRepositoryTest {

  private static final String GLOBAL_ID = "de:05770:1282";
  private static final ServicePointNumber HOLDER = ServicePointNumber.ofNumberWithoutCheckDigit(1105770);
  private static final ServicePointNumber OTHER = ServicePointNumber.ofNumberWithoutCheckDigit(1105771);

  private final ServicePointGlobalIdRepository servicePointGlobalIdRepository;

  @Autowired
  ServicePointGlobalIdRepositoryTest(ServicePointGlobalIdRepository servicePointGlobalIdRepository) {
    this.servicePointGlobalIdRepository = servicePointGlobalIdRepository;
  }

  @AfterEach
  void tearDown() {
    servicePointGlobalIdRepository.deleteAll();
  }

  private void givenMapping(ServicePointNumber servicePointNumber, String globalId) {
    servicePointGlobalIdRepository.saveAndFlush(
        ServicePointGlobalId.builder().servicePointNumber(servicePointNumber).globalId(globalId).build());
  }

  @Test
  @Transactional
  void shouldReleaseTheGlobalIdFromItsHolder() {
    // Given
    givenMapping(HOLDER, GLOBAL_ID);

    // When
    int released = servicePointGlobalIdRepository.releaseGlobalIdFrom(GLOBAL_ID, HOLDER);

    // Then
    assertThat(released).isEqualTo(1);
    assertThat(servicePointGlobalIdRepository.findByGlobalId(GLOBAL_ID)).isEmpty();
  }

  @Test
  @Transactional
  void shouldKeepTheRowWhenItNoLongerHoldsTheGlobalId() {
    // Given the holder was meanwhile reassigned to a different Global-ID
    givenMapping(HOLDER, "de:05770:9999");

    // When
    int released = servicePointGlobalIdRepository.releaseGlobalIdFrom(GLOBAL_ID, HOLDER);

    // Then
    assertThat(released).isZero();
    assertThat(servicePointGlobalIdRepository.findByServicePointNumber(HOLDER)).get()
        .extracting(ServicePointGlobalId::getGlobalId).isEqualTo("de:05770:9999");
  }

  @Test
  @Transactional
  void shouldNotTouchTheGlobalIdOfAnotherServicePoint() {
    // Given
    givenMapping(HOLDER, GLOBAL_ID);

    // When
    int released = servicePointGlobalIdRepository.releaseGlobalIdFrom(GLOBAL_ID, OTHER);

    // Then
    assertThat(released).isZero();
    assertThat(servicePointGlobalIdRepository.findByGlobalId(GLOBAL_ID)).isPresent();
  }
}
