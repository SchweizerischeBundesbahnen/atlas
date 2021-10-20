package ch.sbb.line.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.line.directory.IntegrationTest;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.model.CmykColor;
import ch.sbb.line.directory.model.RgbColor;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class LineVersionRepositoryTest {

  private static final RgbColor RGB_COLOR = new RgbColor(0, 0, 0);
  private static final CmykColor CYMK_COLOR = new CmykColor(0, 0, 0, 0);

  private final LineVersionRepository lineVersionRepository;
  private LineVersion lineVersion;

  @Autowired
  public LineVersionRepositoryTest(LineVersionRepository lineVersionRepository) {
    this.lineVersionRepository = lineVersionRepository;
  }

  @BeforeEach
  void setUpLineVersion() {
    lineVersion = lineVersionRepository.save(LineVersion.builder()
                                                        .status(Status.ACTIVE)
                                                        .type(LineType.ORDERLY)
                                                        .slnid("slnid")
                                                        .paymentType(PaymentType.INTERNATIONAL)
                                                        .shortName("shortName")
                                                        .alternativeName("alternativeName")
                                                        .combinationName("combinationName")
                                                        .longName("longName")
                                                        .colorFontRgb(RGB_COLOR)
                                                        .colorBackRgb(RGB_COLOR)
                                                        .colorFontCmyk(CYMK_COLOR)
                                                        .colorBackCmyk(CYMK_COLOR)
                                                        .description("description")
                                                        .validFrom(LocalDate.of(2020, 12, 12))
                                                        .validTo(LocalDate.of(2099, 12, 12))
                                                        .businessOrganisation("businessOrganisation")
                                                        .comment("comment")
                                                        .swissLineNumber("swissLineNumber")
                                                        .build());
  }

  @Test
  void shouldGetSimpleVersion() {
    //given

    //when
    LineVersion result = lineVersionRepository.findAll().get(0);

    //then
    assertThat(result).usingRecursiveComparison().ignoringActualNullFields().isEqualTo(lineVersion);
  }

  @Test
  void shouldGetCountVersions() {
    //when
    long result = lineVersionRepository.count();

    //then
    assertThat(result).isEqualTo(1);
  }

  @Test
  void shouldDeleteVersion() {
    //given
    lineVersionRepository.delete(lineVersion);

    //when
    List<LineVersion> result = lineVersionRepository.findAll();

    //then
    assertThat(result).isEmpty();
  }
}