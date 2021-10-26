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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class LineVersionRepositoryTest {

  private static final RgbColor RGB_COLOR = new RgbColor(0, 0, 0);
  private static final CmykColor CYMK_COLOR = new CmykColor(0, 0, 0, 0);
  private static final LineVersion LINE_VERSION = createLineVersion();

  private final LineVersionRepository lineVersionRepository;

  @Autowired
  public LineVersionRepositoryTest(LineVersionRepository lineVersionRepository) {
    this.lineVersionRepository = lineVersionRepository;
  }


  @Test
  void shouldGetSimpleVersion() {
    //given
    lineVersionRepository.save(LINE_VERSION);

    //when
    LineVersion result = lineVersionRepository.findAll().get(0);

    //then
    assertThat(result).usingRecursiveComparison()
                      .ignoringActualNullFields()
                      .isEqualTo(LINE_VERSION);
    assertThat(result.getSlnid()).startsWith("ch:1:slnid:");
    assertThat(result.getCreationDate()).isNotNull();
    assertThat(result.getEditionDate()).isNotNull();
  }

  @Test
  void shouldUpdateSimpleLineVersion() {
    //given
    LineVersion result = lineVersionRepository.save(LINE_VERSION);


    //when
    result.setShortName("other shortname");
    result = lineVersionRepository.save(result);

    //then
    assertThat(result.getShortName()).isEqualTo("other shortname");
  }

  @Test
  void shouldGetCountVersions() {
    //given
    lineVersionRepository.save(LINE_VERSION);

    //when
    long result = lineVersionRepository.count();

    //then
    assertThat(result).isEqualTo(1);
  }

  @Test
  void shouldDeleteVersion() {
    //given
    LineVersion lineVersion = lineVersionRepository.save(LINE_VERSION);
    lineVersionRepository.delete(lineVersion);

    //when
    List<LineVersion> result = lineVersionRepository.findAll();

    //then
    assertThat(result).isEmpty();
  }

  private static LineVersion createLineVersion() {
    return LineVersion.builder()
                      .status(Status.ACTIVE)
                      .type(LineType.ORDERLY)
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
                      .businessOrganisation(
                          "businessOrganisation")
                      .comment("comment")
                      .swissLineNumber("swissLineNumber")
                      .build();
  }
}