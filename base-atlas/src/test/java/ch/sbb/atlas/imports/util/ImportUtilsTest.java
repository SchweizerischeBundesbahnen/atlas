package ch.sbb.atlas.imports.util;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.versioning.model.Versionable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;

class ImportUtilsTest {

  @Test
  void shouldFindVersionsExactlyIncludedBetweenEditedValidFromAndEditedValidTo() {
    //given
    ObjVersions version1 = ObjVersions.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();
    ObjVersions version2 = ObjVersions.builder()
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .build();

    //when
    List<ObjVersions> result = ImportUtils.findVersionsExactlyIncludedBetweenEditedValidFromAndEditedValidTo(
        LocalDate.of(2000, 1, 1),
        LocalDate.of(2001, 12, 31),
        List.of(version1, version2)
    );

    //then
    assertThat(result).hasSize(2);
  }

  @Test
  void shouldNotFindVersionsWhenNotExactlyIncludedBetweenEditedValidFromAndEditedValidTo() {
    //given
    ObjVersions version1 = ObjVersions.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();
    ObjVersions version2 = ObjVersions.builder()
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .build();

    //when
    List<ObjVersions> result = ImportUtils.findVersionsExactlyIncludedBetweenEditedValidFromAndEditedValidTo(
        LocalDate.of(2000, 1, 2),
        LocalDate.of(2001, 12, 30),
        List.of(version1, version2)
    );

    //then
    assertThat(result).isEmpty();
  }

  @Test
  void shouldGetCurrentObjVersionsWhenValidFromPerfectMatch() {
    //given
    ObjVersions version = ObjVersions.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();
    ObjVersions edited = ObjVersions.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 30))
        .build();
    List<ObjVersions> versions = new ArrayList<>();
    versions.add(version);
    //when
    ObjVersions result = ImportUtils.getCurrentVersion(versions, edited.getValidFrom(), edited.getValidTo());
    //then
    AssertionsForClassTypes.assertThat(result).isNotNull();
  }

  @Test
  void shouldGetCurrentObjVersionsWhenValidToPerfectMatch() {
    //given
    ObjVersions version = ObjVersions.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();
    ObjVersions edited = ObjVersions.builder()
        .validFrom(LocalDate.of(2000, 1, 2))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();
    List<ObjVersions> versions = new ArrayList<>();
    versions.add(version);
    //when
    ObjVersions result = ImportUtils.getCurrentVersion(versions, edited.getValidFrom(), edited.getValidTo());
    //then
    AssertionsForClassTypes.assertThat(result).isNotNull();
  }

  @Test
  void shouldGetCurrentObjVersionsWhenEditedVersionIsBetweenDbVersion() {
    //given
    ObjVersions version = ObjVersions.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();
    ObjVersions edited = ObjVersions.builder()
        .validFrom(LocalDate.of(2000, 1, 2))
        .validTo(LocalDate.of(2000, 12, 30))
        .build();
    List<ObjVersions> versions = new ArrayList<>();
    versions.add(version);
    //when
    ObjVersions result = ImportUtils.getCurrentVersion(versions, edited.getValidFrom(), edited.getValidTo());
    //then
    AssertionsForClassTypes.assertThat(result).isNotNull();
  }

  @Test
  void shouldGetCurrentObjVersionsWhenFoundMultipleVersionWhenEditedVersionIsBetweenDbVersion() {
    //given
    ObjVersions version1 = ObjVersions.builder()
        .validFrom(LocalDate.of(2000, 1, 2))
        .validTo(LocalDate.of(2000, 6, 1))
        .build();
    ObjVersions version2 = ObjVersions.builder()
        .validFrom(LocalDate.of(2000, 6, 2))
        .validTo(LocalDate.of(2000, 12, 30))
        .build();
    ObjVersions edited = ObjVersions.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();
    List<ObjVersions> versions = new ArrayList<>();
    versions.add(version1);
    versions.add(version2);
    //when
    ObjVersions result = ImportUtils.getCurrentVersion(versions, edited.getValidFrom(), edited.getValidTo());
    //then
    AssertionsForClassTypes.assertThat(result).isNotNull();
  }

  /**
   * given |------------|-----------|
   * edit                             |-----------|
   * return             |-----------|
   */
  @Test
  void shouldGetCurrentObjVersionsWhenNoCurrentVersionMatchedAndReturnTheLastVersion() {
    //given
    ObjVersions version1 = ObjVersions.builder()
        .validFrom(LocalDate.of(2000, 1, 2))
        .validTo(LocalDate.of(2000, 6, 1))
        .build();
    ObjVersions version2 = ObjVersions.builder()
        .validFrom(LocalDate.of(2000, 6, 2))
        .validTo(LocalDate.of(2000, 12, 30))
        .build();
    ObjVersions edited = ObjVersions.builder()
        .validFrom(LocalDate.of(2000, 12, 31))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();
    List<ObjVersions> versions = new ArrayList<>();
    versions.add(version1);
    versions.add(version2);
    //when
    ObjVersions result = ImportUtils.getCurrentVersion(versions, edited.getValidFrom(), edited.getValidTo());
    //then
    AssertionsForClassTypes.assertThat(result).isNotNull();
    AssertionsForClassTypes.assertThat(result.getValidFrom()).isEqualTo(LocalDate.of(2000, 6, 2));
    AssertionsForClassTypes.assertThat(result.getValidTo()).isEqualTo(LocalDate.of(2000, 12, 30));
  }

  /**
   * given               |------------|-----------|
   * edit  |-----------|
   * return             |------------|
   */
  @Test
  void shouldGetCurrentObjVersionsWhenNoCurrentVersionMatchedAndReturnTheFirstVersion() {
    //given
    ObjVersions version1 = ObjVersions.builder()
        .validFrom(LocalDate.of(2000, 1, 2))
        .validTo(LocalDate.of(2000, 6, 1))
        .build();
    ObjVersions version2 = ObjVersions.builder()
        .validFrom(LocalDate.of(2000, 6, 2))
        .validTo(LocalDate.of(2000, 12, 30))
        .build();
    ObjVersions edited = ObjVersions.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 1, 1))
        .build();
    List<ObjVersions> versions = new ArrayList<>();
    versions.add(version1);
    versions.add(version2);
    //when
    ObjVersions result = ImportUtils.getCurrentVersion(versions, edited.getValidFrom(), edited.getValidTo());
    //then
    AssertionsForClassTypes.assertThat(result).isNotNull();
    AssertionsForClassTypes.assertThat(result.getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 2));
    AssertionsForClassTypes.assertThat(result.getValidTo()).isEqualTo(LocalDate.of(2000, 6, 1));
  }

  /**
   * given  |------------|-----------|
   * edit   |------------------------|
   * return |------------|-----------|
   */
  @Test
  void shouldReturnTheFirstVersionWhenTheEditVersionMatchExactlyMoreThenOneVersion() {
    //given
    ObjVersions version1 = ObjVersions.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 6, 1))
        .build();
    ObjVersions version2 = ObjVersions.builder()
        .validFrom(LocalDate.of(2000, 6, 2))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();
    ObjVersions edited = ObjVersions.builder()
        .validFrom(LocalDate.of(2000, 1, 2))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();
    List<ObjVersions> versions = new ArrayList<>();
    versions.add(version1);
    versions.add(version2);
    //when
    ObjVersions result = ImportUtils.getCurrentVersion(versions, edited.getValidFrom(), edited.getValidTo());
    AssertionsForClassTypes.assertThat(result).isNotNull();
  }

  @Data
  @Builder
  static class ObjVersions implements Versionable {

    private Long id;
    private LocalDate validFrom;
    private LocalDate validTo;

  }

}
