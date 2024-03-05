package ch.sbb.prm.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointAttributeType;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class ReferencePointRepositoryTest {
  private static final String PARENT_SERVICE_POINT_SLOID = "ch:1:sloid:70000";
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
  void shouldReturnReferencePoints() {
    //when
    List<ReferencePointVersion> result = referencePointRepository.findAll();
    //then
    assertThat(result).hasSize(1);
  }

  @Test
  void shouldReturnReferencePointsNotInStatusRevoked() {
    //given
    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(8576332);
    ReferencePointVersion ref1 = ReferencePointVersion.builder()
        .sloid("ch:1:sloid:76332:103")
        .status(Status.VALIDATED)
        .number(servicePointNumber)
        .validFrom(LocalDate.of(2024, 2, 19))
        .validTo(LocalDate.of(2026, 2, 19))
        .designation("Hermione")
        .additionalInformation(null)
        .mainReferencePoint(true)
        .parentServicePointSloid(PARENT_SERVICE_POINT_SLOID)
        .referencePointType(ReferencePointAttributeType.ASSISTANCE_POINT)
        .build();
    ReferencePointVersion ref2 = ReferencePointVersion.builder()
        .sloid("ch:1:sloid:76332:104")
        .status(Status.REVOKED)
        .number(servicePointNumber)
        .validFrom(LocalDate.of(2025, 2, 2))
        .validTo(LocalDate.of(2025, 2, 19))
        .designation("Voldemort")
        .additionalInformation(null)
        .mainReferencePoint(true)
        .parentServicePointSloid(PARENT_SERVICE_POINT_SLOID)
        .referencePointType(ReferencePointAttributeType.ASSISTANCE_POINT)
        .build();
    ReferencePointVersion ref3 = ReferencePointVersion.builder()
        .sloid("ch:1:sloid:76332:105")
        .status(Status.VALIDATED)
        .number(servicePointNumber)
        .validFrom(LocalDate.of(2026, 2, 2))
        .validTo(LocalDate.of(2026, 2, 19))
        .designation("Harry Potter")
        .additionalInformation(null)
        .mainReferencePoint(true)
        .parentServicePointSloid(PARENT_SERVICE_POINT_SLOID)
        .referencePointType(ReferencePointAttributeType.ASSISTANCE_POINT)
        .build();
    referencePointRepository.saveAndFlush(ref1);
    referencePointRepository.saveAndFlush(ref2);
    referencePointRepository.saveAndFlush(ref3);

    //when
    List<ReferencePointVersion> result = referencePointRepository.findMainReferencePointOverlaps(ref1);
    //then
    assertThat(result).hasSize(1).containsExactlyInAnyOrder(ref3);
  }

}