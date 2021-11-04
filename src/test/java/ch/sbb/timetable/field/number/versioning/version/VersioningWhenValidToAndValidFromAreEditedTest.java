package ch.sbb.timetable.field.number.versioning.version;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.timetable.field.number.BaseTest;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

public class VersioningWhenValidToAndValidFromAreEditedTest extends BaseTest {

  private final VersioningWhenValidToAndValidFromAreEdited versioningWhenValidToAndValidFromAreEdited = new VersioningWhenValidToAndValidFromAreEdited();

  @Test
  public void shouldFindObjectToVersioningIfEditedValidFromIsEqualToCurrentValidTo(){
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning = ToVersioning.builder().versionable(versionableObject1).build();
    List<ToVersioning> objectsToVersioning = List.of(toVersioning);
    LocalDate editedValidFrom = LocalDate.of(2021, 12, 31);
    LocalDate editedValidTo = LocalDate.of(2022, 12, 31);
    //when
    List<ToVersioning> result = versioningWhenValidToAndValidFromAreEdited.findObjectToVersioningInValidFromValidToRange(
        objectsToVersioning, editedValidFrom, editedValidTo);
    assertThat(result).isNotNull();

    //then

  }

}