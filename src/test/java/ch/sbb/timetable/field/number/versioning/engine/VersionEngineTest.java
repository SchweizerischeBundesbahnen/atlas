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

  @Test
  public void shouldReturnTrueWhenOnlyValidFromIsEdited(){
    //given
    VersionableObject edited = VersionableObject.builder()
                                                .validFrom(LocalDate.of(2000, 1, 2))
                                                .build();
    VersionableObject current = VersionableObject.builder()
                                                 .validFrom(LocalDate.of(2000, 1, 1))
                                                 .validTo(LocalDate.of(2000, 12, 31))
                                                 .build();
    //when
    boolean result = versioningEngine.isOnlyValidFromEdited(current, edited);

    //then
    assertThat(result).isTrue();

  }

  @Test
  public void shouldReturnTrueWhenOnlyValidFromIsEditedAndEditedValidToIsEqualToCurrentValidTo(){
    //given
    VersionableObject edited = VersionableObject.builder()
                                                .validFrom(LocalDate.of(2000, 1, 2))
                                                .validTo(LocalDate.of(2000, 12, 31))
                                                .build();
    VersionableObject current = VersionableObject.builder()
                                                 .validFrom(LocalDate.of(2000, 1, 1))
                                                 .validTo(LocalDate.of(2000, 12, 31))
                                                 .build();
    //when
    boolean result = versioningEngine.isOnlyValidFromEdited(current, edited);

    //then
    assertThat(result).isTrue();

  }

  @Test
  public void shouldReturnTrueWhenOnlyValidFromToEdited(){
    //given
    VersionableObject edited = VersionableObject.builder()
                                                .validTo(LocalDate.of(2001, 1, 1))
                                                .build();
    VersionableObject current = VersionableObject.builder()
                                                 .validFrom(LocalDate.of(2000, 1, 1))
                                                 .validTo(LocalDate.of(2000, 12, 31))
                                                 .build();
    //when
    boolean result = versioningEngine.isOnlyValidToEdited(current, edited);

    //then
    assertThat(result).isTrue();

  }

  @Test
  public void shouldReturnTrueWhenOnlyValidToIsEditedAndEditedValidFromIsEqualToCurrentValidFrom(){
    //given
    VersionableObject edited = VersionableObject.builder()
                                                .validFrom(LocalDate.of(2000, 1, 2))
                                                .validTo(LocalDate.of(2001, 12, 31))
                                                .build();
    VersionableObject current = VersionableObject.builder()
                                                 .validFrom(LocalDate.of(2000, 1, 2))
                                                 .validTo(LocalDate.of(2000, 12, 31))
                                                 .build();
    //when
    boolean result = versioningEngine.isOnlyValidToEdited(current, edited);

    //then
    assertThat(result).isTrue();

  }

  @Test
  public void shouldReturnTrueWhenValidFromAndValidToAreEdited(){
    //given
    VersionableObject edited = VersionableObject.builder()
                                                .validFrom(LocalDate.of(2000, 1, 2))
                                                .validTo(LocalDate.of(2001, 12, 31))
                                                .build();
    //when
    boolean result = versioningEngine.areValidFromAndValidToEdited(edited);

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnFalseWhenOnlyValidToIsEdited(){
    //given
    VersionableObject edited = VersionableObject.builder()
                                                .validTo(LocalDate.of(2001, 12, 31))
                                                .build();
    //when
    boolean result = versioningEngine.areValidFromAndValidToEdited(edited);

    //then
    assertThat(result).isFalse();
  }

  @Test
  public void shouldReturnFalseWhenOnlyValidFromIsEdited(){
    //given
    VersionableObject edited = VersionableObject.builder()
                                                .validFrom(LocalDate.of(2000, 1, 2))
                                                .build();
    //when
    boolean result = versioningEngine.areValidFromAndValidToEdited(edited);

    //then
    assertThat(result).isFalse();
  }

}
