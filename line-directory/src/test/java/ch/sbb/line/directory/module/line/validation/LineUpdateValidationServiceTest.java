package ch.sbb.line.directory.module.line.validation;

import static ch.sbb.atlas.api.lidi.enumaration.LineType.ORDERLY;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.line.directory.module.line.LineTestData;
import ch.sbb.line.directory.module.line.entity.LineVersion;
import ch.sbb.line.directory.module.line.exception.LineFieldNotUpdatableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.MockitoAnnotations;

class LineUpdateValidationServiceTest {

  private LineUpdateValidationService lineUpdateValidationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    lineUpdateValidationService = new LineUpdateValidationService();
  }

  @ParameterizedTest
  @EnumSource(value = LineType.class, names = {"DISPOSITION", "OPERATIONAL", "TEMPORARY"})
  void shouldNotUpdateLineWithTypeNotOrderlyWhenUpdateSwissLineNumber(LineType lineType) {

    LineVersion currentLineVersion = LineTestData.lineVersionBuilder().lineType(lineType).swissLineNumber(null).build();
    LineVersion editedLineVersion =
        LineTestData.lineVersionBuilder().lineType(lineType).swissLineNumber("IC2").build();

    assertThrows(LineFieldNotUpdatableException.class,
        () -> lineUpdateValidationService.validateFieldsNotUpdatableForLineTypeOrderly(currentLineVersion, editedLineVersion));
  }

  @Test
  void shouldUpdateLineWithTypeOrderlyWhenUpdateSwissLineNumber() {

    LineVersion currentLineVersion = LineTestData.lineVersionBuilder().lineType(ORDERLY).swissLineNumber(null).build();
    LineVersion editedLineVersion =
        LineTestData.lineVersionBuilder().lineType(ORDERLY).swissLineNumber("IC2").build();

    assertThatNoException().isThrownBy(
        () -> lineUpdateValidationService.validateFieldsNotUpdatableForLineTypeOrderly(currentLineVersion, editedLineVersion));
  }

  @ParameterizedTest
  @EnumSource(value = LineType.class, names = {"DISPOSITION", "OPERATIONAL", "TEMPORARY"})
  void shouldNotUpdateLineWithTypeNotOrderlyWhenUpdateLineConcessionType(LineType lineType) {

    LineVersion currentLineVersion = LineTestData.lineVersionBuilder().lineType(lineType).concessionType(null).build();
    LineVersion editedLineVersion =
        LineTestData.lineVersionBuilder().lineType(lineType).swissLineNumber("IC2").concessionType(LineConcessionType.LINE_ABROAD)
            .build();

    assertThrows(LineFieldNotUpdatableException.class,
        () -> lineUpdateValidationService.validateFieldsNotUpdatableForLineTypeOrderly(currentLineVersion, editedLineVersion));
  }

  @Test
  void shouldUpdateLineWithTypeOrderlyWhenUpdateLineConcessionType() {

    LineVersion currentLineVersion = LineTestData.lineVersionBuilder().lineType(ORDERLY)
        .concessionType(LineConcessionType.COLLECTION_LINE).build();
    LineVersion editedLineVersion =
        LineTestData.lineVersionBuilder().lineType(ORDERLY).concessionType(LineConcessionType.COLLECTION_LINE)
            .swissLineNumber("IC2").build();

    assertThatNoException().isThrownBy(
        () -> lineUpdateValidationService.validateFieldsNotUpdatableForLineTypeOrderly(currentLineVersion, editedLineVersion));
  }

}