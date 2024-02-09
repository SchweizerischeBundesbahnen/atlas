package ch.sbb.prm.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.exception.MainReferencePointConflictException;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class ReferencePointValidationServiceTest {

  private static final String PARENT_SERVICE_POINT_SLOID = "ch:1:sloid:70000";

  private final ReferencePointValidationService referencePointValidationService;
  private final ReferencePointRepository referencePointRepository;

  @Autowired
  ReferencePointValidationServiceTest(ReferencePointValidationService referencePointValidationService,
      ReferencePointRepository referencePointRepository) {
    this.referencePointValidationService = referencePointValidationService;
    this.referencePointRepository = referencePointRepository;
  }

  @Test
  void shouldNotAllowMainReferencePointOnSamePeriod() {
    // Given
    ReferencePointVersion referencePointVersion1 = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion1.setMainReferencePoint(true);
    referencePointVersion1.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion1);

    ReferencePointVersion referencePointVersion2 = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion2.setSloid("ch:1:sloid:12345:12");
    referencePointVersion2.setMainReferencePoint(true);
    referencePointVersion2.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);

    // When
    MainReferencePointConflictException mainReferencePointConflictException = assertThrows(
        MainReferencePointConflictException.class,
        () -> referencePointValidationService.validatePreconditionBusinessRule(referencePointVersion2));

    // Then
    assertThat(mainReferencePointConflictException.getErrorResponse().getDetails().first().getMessage()).isEqualTo(
        "Main ReferencePoint already taken from 01.01.2000 to 31.12.2000 by ch:1:sloid:12345:1");
  }

  @Test
  void shouldAllowNonMainReferencePointOnSamePeriod() {
    // Given
    ReferencePointVersion referencePointVersion1 = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion1.setMainReferencePoint(true);
    referencePointVersion1.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion1);

    ReferencePointVersion referencePointVersion2 = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion2.setSloid("ch:1:sloid:12345:12");
    referencePointVersion2.setMainReferencePoint(false);
    referencePointVersion2.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);

    // When
    assertThatNoException().isThrownBy(
        () -> referencePointValidationService.validatePreconditionBusinessRule(referencePointVersion2));
  }

  @Test
  void shouldAllowMainReferencePointOnDifferentPeriod() {
    // Given
    ReferencePointVersion referencePointVersion1 = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion1.setMainReferencePoint(true);
    referencePointVersion1.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion1);

    ReferencePointVersion referencePointVersion2 = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion2.setSloid("ch:1:sloid:12345:12");
    referencePointVersion2.setValidFrom(LocalDate.of(2021, 1, 1));
    referencePointVersion2.setValidTo(LocalDate.of(2021, 12, 31));
    referencePointVersion2.setMainReferencePoint(true);
    referencePointVersion2.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);

    // When
    assertThatNoException().isThrownBy(
        () -> referencePointValidationService.validatePreconditionBusinessRule(referencePointVersion2));
  }

}