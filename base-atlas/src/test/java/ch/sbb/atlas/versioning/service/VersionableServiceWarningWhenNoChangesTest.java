package ch.sbb.atlas.versioning.service;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import ch.sbb.atlas.versioning.exception.VersioningNoChangesException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class VersionableServiceWarningWhenNoChangesTest extends VersionableServiceBaseTest {

  /**
   * Szenario: auf version1 validTo gleich wie version2 validTo setzen ohne properties ändern
   * NEU:      |_______________________________________________________|
   * IST:      |----------------------|--------------------------------|
   * Version:        1                                2
   *
   * RESULTAT: |----------------------|--------------------------------|
   * Version:        1                                2
   * Es wird keine änderung stattfinden
   */
  @Test
   void scenario1a() {
    //given
    VersionableObject editedVersion = VersionableObject.builder().build();
    editedVersion.setValidFrom(versionableObject1.getValidFrom());
    editedVersion.setValidTo(versionableObject2.getValidTo());

    //when
    assertThatExceptionOfType(VersioningNoChangesException.class).isThrownBy(
        () -> versionableService.versioningObjects(versionableObject2, editedVersion,
            Arrays.asList(versionableObject1, versionableObject2)));
  }

}