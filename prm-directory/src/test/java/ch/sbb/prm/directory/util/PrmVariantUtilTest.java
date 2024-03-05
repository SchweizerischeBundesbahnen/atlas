package ch.sbb.prm.directory.util;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.StopPointVersion;
import org.junit.jupiter.api.Test;

class PrmVariantUtilTest {

  @Test
  void shouldReturnTrueWhenChangingFromReducedToComplete() {
    //given
    StopPointVersion reduced = StopPointTestData.builderVersionReduced().build();
    StopPointVersion complete = StopPointTestData.builderVersionCompleteFull().build();

    //when
    boolean result = PrmVariantUtil.isChangingFromReducedToComplete(reduced, complete);

    //then
    assertThat(result).isTrue();
  }

  @Test
  void shouldReturnTrueWhenChangingFromCompleteToReduced() {
    //given
    StopPointVersion reduced = StopPointTestData.builderVersionReduced().build();
    StopPointVersion complete = StopPointTestData.builderVersionCompleteFull().build();

    //when
    boolean result = PrmVariantUtil.isChangingFromCompleteToReduced(complete, reduced);

    //then
    assertThat(result).isTrue();
  }

  @Test
  void shouldReturnTrueWhenIsVariantChanging() {
    //given
    StopPointVersion reduced = StopPointTestData.builderVersionReduced().build();
    StopPointVersion complete = StopPointTestData.builderVersionCompleteFull().build();

    //when
    boolean result = PrmVariantUtil.isPrmVariantChanging(complete, reduced);

    //then
    assertThat(result).isTrue();
  }

  @Test
  void shouldReturnFalseWhenVariantCompleteIsNotChanging() {
    //given
    StopPointVersion complete = StopPointTestData.builderVersionCompleteFull().build();

    //when
    boolean result = PrmVariantUtil.isPrmVariantChanging(complete, complete);

    //then
    assertThat(result).isFalse();
  }

  @Test
  void shouldReturnFalseWhenVariantReducedIsNotChanging() {
    //given
    StopPointVersion reduced = StopPointTestData.builderVersionReduced().build();

    //when
    boolean result = PrmVariantUtil.isPrmVariantChanging(reduced, reduced);

    //then
    assertThat(result).isFalse();
  }

}