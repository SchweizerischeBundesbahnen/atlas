package ch.sbb.line.directory.module.line.security;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.enumaration.v3.Financing;
import ch.sbb.line.directory.module.line.LineVersionV3TestData;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import org.junit.jupiter.api.Test;

class LineV3RestrictedFieldServiceTest {

  private final LineV3RestrictedFieldService restrictedFieldService = new LineV3RestrictedFieldService();

  @Test
  void shouldDetectEmptyRestrictedFields() {
    LineVersionV3 withoutRestrictedFields = LineVersionV3TestData.lineBuilder().financing(null).build();

    assertThat(restrictedFieldService.areRestrictedFieldsEmpty(withoutRestrictedFields)).isTrue();
  }

  @Test
  void shouldDetectFinancing() {
    LineVersionV3 withFinancing = LineVersionV3TestData.lineBuilder().financing(Financing.WBV).build();

    assertThat(restrictedFieldService.areRestrictedFieldsEmpty(withFinancing)).isFalse();
  }

  @Test
  void shouldDetectCablewayField() {
    LineVersionV3 withCablewayName = LineVersionV3TestData.lineBuilder().financing(null).cablewayName("Pendelbahn").build();

    assertThat(restrictedFieldService.areRestrictedFieldsEmpty(withCablewayName)).isFalse();
  }

  @Test
  void shouldAcceptResentUnchangedRestrictedFields() {
    LineVersionV3 current = LineVersionV3TestData.cableway();
    LineVersionV3 edited = current.toBuilder()
        .description("Neue Beschreibung")
        .cablewayNumber(new String(current.getCablewayNumber()))
        .cablewayType(new String(current.getCablewayType()))
        .cablewayName(new String(current.getCablewayName()))
        .build();

    assertThat(restrictedFieldService.areRestrictedFieldsUnchanged(current, edited)).isTrue();
  }

  @Test
  void shouldDetectChangedFinancing() {
    LineVersionV3 current = LineVersionV3TestData.line();
    LineVersionV3 edited = current.toBuilder().financing(Financing.WBV).build();

    assertThat(restrictedFieldService.areRestrictedFieldsUnchanged(current, edited)).isFalse();
  }

  @Test
  void shouldDetectClearedRestrictedField() {
    LineVersionV3 current = LineVersionV3TestData.cableway();
    LineVersionV3 edited = current.toBuilder().cablewayName(null).build();

    assertThat(restrictedFieldService.areRestrictedFieldsUnchanged(current, edited)).isFalse();
  }

  @Test
  void shouldDetectAddedRestrictedField() {
    LineVersionV3 current = LineVersionV3TestData.lineBuilder().financing(null).build();
    LineVersionV3 edited = current.toBuilder().cablewayType("Pendelbahn").build();

    assertThat(restrictedFieldService.areRestrictedFieldsUnchanged(current, edited)).isFalse();
  }

}
