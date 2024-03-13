package ch.sbb.atlas.imports.util;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.ImportDataModifier;
import ch.sbb.atlas.versioning.model.Versionable;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

  @Test
  void shouldReplaceNewLinesCorrectlyOnlyWhen$newline$ExistsAndUpdateModifiedDateOnlyWhenReplacementTookPlace()
      throws IllegalAccessException {
    // given
    List<CsvModel> csvModels = List.of(
        CsvModel.builder().description("Desc $newline$ of $newline$ this model.").comment("Comment$newline$ with one newline.")
            .editionDate(LocalDateTime.of(2020, 12, 15, 10, 15)).build(),
        CsvModel.builder().description("Desc without newlines.").comment("Comment without newlines.")
            .editionDate(LocalDateTime.of(2020, 12, 15, 10, 15)).build()
    );

    // when
    ImportUtils.replaceNewLines(csvModels);

    // then
    assertThat(csvModels).hasSize(2);

    assertThat(csvModels.get(0).comment).isEqualTo("Comment\r\n with one newline.");
    assertThat(csvModels.get(0).description).isEqualTo("Desc \r\n of \r\n this model.");
    assertThat(csvModels.get(0).editionDate).isEqualToIgnoringSeconds(LocalDateTime.now());

    assertThat(csvModels.get(1).comment).isEqualTo("Comment without newlines.");
    assertThat(csvModels.get(1).description).isEqualTo("Desc without newlines.");
    assertThat(csvModels.get(1).editionDate).isEqualTo(LocalDateTime.of(2020, 12, 15, 10, 15));
  }


  @Test
  void shouldReplaceNewLinesCorrectlyOnlyWhen$newline$ExistsAndUpdateModifiedDateOnlyWhenReplacementTookPlace1()
      throws IllegalAccessException {
    // given
    List<CsvModel> csvModels = List.of(
        CsvModel.builder().description("SBA.71.012.1$newline$$newline$Anlagen sind seit mehreren Jahren ausser Betrieb aber nicht zurückgebaut.").comment("Comment$newline$ with one newline.")
            .editionDate(LocalDateTime.of(2020, 12, 15, 10, 15)).build(),
        CsvModel.builder().description("Desc without newlines.").comment("Comment without newlines.")
            .editionDate(LocalDateTime.of(2020, 12, 15, 10, 15)).build()
    );

    // when
    ImportUtils.replaceNewLines(csvModels);

    // then
    assertThat(csvModels).hasSize(2);

    assertThat(csvModels.get(0).comment).isEqualTo("Comment\r\n with one newline.");
    assertThat(csvModels.get(0).description).isEqualTo("SBA.71.012.1\r\n\r\nAnlagen sind seit mehreren Jahren ausser Betrieb "
        + "aber nicht zurückgebaut.");
    assertThat(csvModels.get(0).editionDate).isEqualToIgnoringSeconds(LocalDateTime.now());

    assertThat(csvModels.get(1).comment).isEqualTo("Comment without newlines.");
    assertThat(csvModels.get(1).description).isEqualTo("Desc without newlines.");
    assertThat(csvModels.get(1).editionDate).isEqualTo(LocalDateTime.of(2020, 12, 15, 10, 15));
  }

  @Test
  void shouldReplaceDidokHighestDateWithAtlas() {
    // given
    List<CsvModel> csvModels = List.of(
        CsvModel.builder()
            .editionDate(LocalDateTime.of(2020, 12, 15, 10, 15))
            .validTo(LocalDate.of(2099,12,31))
            .build(),
        CsvModel.builder()
            .editionDate(LocalDateTime.of(2020, 12, 15, 10, 15))
            .validTo(LocalDate.of(2024,12,31))
            .build()
    );

    // when
    ImportUtils.replaceToDateWithHighestDate(csvModels);

    // then
    assertThat(csvModels).hasSize(2);

    assertThat(csvModels.get(0).validTo).isEqualTo(ImportUtils.ATLAS_HIGHEST_DATE);
    assertThat(csvModels.get(0).editionDate).isEqualToIgnoringSeconds(LocalDateTime.now());
    assertThat(csvModels.get(1).validTo).isEqualTo(LocalDate.of(2024,12,31));
    assertThat(csvModels.get(1).editionDate).isEqualTo(LocalDateTime.of(2020, 12, 15, 10, 15));
  }

  @Builder
  private static class CsvModel implements ImportDataModifier {

    private LocalDateTime editionDate;
    private String description;
    private String comment;
    private LocalDate validTo;

    @Override
    public LocalDate getValidTo() {
      return this.validTo;
    }

    @Override
    public void setValidTo(LocalDate validTo) {
      this.validTo = validTo;
    }

    @Override
    public void setLastModifiedToNow() {
      editionDate = LocalDateTime.now();
    }

  }

  @Data
  @Builder
  static class ObjVersions implements Versionable {

    private Long id;
    private LocalDate validFrom;
    private LocalDate validTo;

  }

}
