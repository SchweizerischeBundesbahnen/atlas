package ch.sbb.line.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.line.directory.IntegrationTest;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.Status;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class SublineVersionRepositoryTest {

  private final SublineVersionRepository sublineVersionRepository;
  private SublineVersion sublineVersion;

  @Autowired
  public SublineVersionRepositoryTest(SublineVersionRepository sublineVersionRepository) {
    this.sublineVersionRepository = sublineVersionRepository;
  }

  @BeforeEach
  void setUpLineVersion() {
    sublineVersion = sublineVersionRepository.save(SublineVersion.builder()
                                                                 .status(Status.ACTIVE)
                                                                 .paymentType(
                                                                     PaymentType.INTERNATIONAL)
                                                                 .shortName("shortName")
                                                                 .longName("longName")
                                                                 .description("description")
                                                                 .validFrom(
                                                                     LocalDate.of(2020, 12, 12))
                                                                 .validTo(
                                                                     LocalDate.of(2099, 12, 12))
                                                                 .businessOrganisation(
                                                                     "businessOrganisation")
                                                                 .swissLineNumber("swissLineNumber")
                                                                 .build());
  }

  @Test
  void shouldGetSimpleVersion() {
    //given

    //when
    SublineVersion result = sublineVersionRepository.findAll().get(0);

    //then
    assertThat(result).usingRecursiveComparison().ignoringActualNullFields().isEqualTo(
        sublineVersion);
    assertThat(result.getSlnid()).startsWith("ch:1:slnid:");
  }

  @Test
  void shouldGetCountVersions() {
    //when
    long result = sublineVersionRepository.count();

    //then
    assertThat(result).isEqualTo(1);
  }

  @Test
  void shouldDeleteVersion() {
    //given
    sublineVersionRepository.delete(sublineVersion);

    //when
    List<SublineVersion> result = sublineVersionRepository.findAll();

    //then
    assertThat(result).isEmpty();
  }
}