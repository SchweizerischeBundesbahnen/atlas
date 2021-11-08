package ch.sbb.timetable.field.number.versioning.engine;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.timetable.field.number.BaseTest.VersionableObject;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class VersionEngineTest  {

  private final VersioningEngine versioningEngine = new VersioningEngine();

  @Test
  public void shouldReturnTrueWhenValidFromAndValidToAreNotEdited() {
    //given
    VersionableObject edited = VersionableObject.builder().build();
    VersionableObject current = VersionableObject.builder()
                                                 .validFrom(LocalDate.of(2000, 1, 1))
                                                 .validTo(LocalDate.of(2000, 12, 31))
                                                 .build();
    //when
    boolean result = versioningEngine.areValidToAndValidFromNotEdited(current, edited);

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnFalseWhenEditedValidFromIsEqualToCurrentValidFromAndValidToIsNotEdited() {
    //given
    VersionableObject edited = VersionableObject.builder()
                                                .validFrom(LocalDate.of(2000, 1, 1))
                                                .build();
    VersionableObject current = VersionableObject.builder()
                                                 .validFrom(LocalDate.of(2000, 1, 1))
                                                 .validTo(LocalDate.of(2000, 12, 31))
                                                 .build();
    //when
    boolean result = versioningEngine.areValidToAndValidFromNotEdited(current, edited);

    //then
    assertThat(result).isFalse();
  }

  @Test
  public void shouldReturnFalseWhenValidFromIsNotEditedAndEditedValidToIsEqualToCurrentValidTo() {
    //given
    VersionableObject edited = VersionableObject.builder()
                                                .validTo(LocalDate.of(2000, 12, 31))
                                                .build();
    VersionableObject current = VersionableObject.builder()
                                                 .validFrom(LocalDate.of(2000, 1, 1))
                                                 .validTo(LocalDate.of(2000, 12, 31))
                                                 .build();
    //when
    boolean result = versioningEngine.areValidToAndValidFromNotEdited(current, edited);

    //then
    assertThat(result).isFalse();
  }

  @Test
  public void shouldReturnTrueWhenCurrentAndEditedValidFrom_ValidToAreEquals() {
    //given
    VersionableObject edited = VersionableObject.builder()
                                                .validFrom(LocalDate.of(2000, 1, 1))
                                                .validTo(LocalDate.of(2000, 12, 31))
                                                .build();
    VersionableObject current = VersionableObject.builder()
                                                 .validFrom(LocalDate.of(2000, 1, 1))
                                                 .validTo(LocalDate.of(2000, 12, 31))
                                                 .build();
    //when
    boolean result = versioningEngine.areValidToAndValidFromNotEdited(current, edited);

    //then
    assertThat(result).isTrue();
  }

}
