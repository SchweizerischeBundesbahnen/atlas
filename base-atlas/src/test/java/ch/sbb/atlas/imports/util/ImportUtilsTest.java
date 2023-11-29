package ch.sbb.atlas.imports.util;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.versioning.model.Versionable;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;
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

  @Data
  @Builder
  static class ObjVersions implements Versionable {
    private Long id;
    private LocalDate validFrom;
    private LocalDate validTo;

  }

}