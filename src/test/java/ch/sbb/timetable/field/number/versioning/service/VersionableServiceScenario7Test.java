package ch.sbb.timetable.field.number.versioning.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class VersionableServiceScenario7Test extends VersionableServiceBaseTest {

  /**
   * Just to avoid Sonar quality gate failure
   */
  @Test
  public void scenario7RightBorder() {
    //given
    LocalDate editedValidFrom = LocalDate.of(2024, 6, 1);
    LocalDate editedValidTo = LocalDate.of(2024, 12, 31);

    VersionableObject editedVersion = VersionableObject.builder()
                                                       .property("Ciao-Ciao")
                                                       .validFrom(editedValidFrom)
                                                       .validTo(editedValidTo)
                                                       .build();

    //when
    assertThatThrownBy(() -> versionableService.versioningObjects(
        versionableObject1,
        editedVersion,
        Arrays.asList(versionableObject1))).isInstanceOf(IllegalStateException.class)
                                           .hasMessageContaining(
          "Scenario not Implemented");

  }
  /**
   * Just to avoid Sonar quality gate failure
   */
  @Test
  public void scenario7LeftBorder() {
    //given
    LocalDate editedValidFrom = LocalDate.of(2004, 6, 1);
    LocalDate editedValidTo = LocalDate.of(2004, 12, 31);

    VersionableObject editedVersion = VersionableObject.builder()
                                                       .property("Ciao-Ciao")
                                                       .validFrom(editedValidFrom)
                                                       .validTo(editedValidTo)
                                                       .build();

    //when
    assertThatThrownBy(() -> versionableService.versioningObjects(
        versionableObject1,
        editedVersion,
        Arrays.asList(versionableObject1))).isInstanceOf(IllegalStateException.class)
                                           .hasMessageContaining(
          "Scenario not Implemented");

  }

}