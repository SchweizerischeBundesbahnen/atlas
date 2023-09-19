package ch.sbb.atlas.model.validation;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.validation.DateValidations;
import ch.sbb.atlas.versioning.model.Versionable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

class DateValidationsTest {

  @Test
  void versionsAreOverlappingOverStart() {
    DummyVersionable v1 = DummyVersionable.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();

    DummyVersionable v2 = DummyVersionable.builder()
        .validFrom(LocalDate.of(1999, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .build();

    List<DummyVersionable> versions = Arrays.asList(v1, v2);
    assertThat(DateValidations.areOverlapping(versions)).isTrue();
  }

  @Test
  void versionsAreOverlappingOverEnd() {
    DummyVersionable v1 = DummyVersionable.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();

    DummyVersionable v2 = DummyVersionable.builder()
        .validFrom(LocalDate.of(2000, 5, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .build();

    List<DummyVersionable> versions = Arrays.asList(v1, v2);
    assertThat(DateValidations.areOverlapping(versions)).isTrue();
  }

  @Test
  void versionsAreOverlappingCompletely() {
    DummyVersionable v1 = DummyVersionable.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();

    DummyVersionable v2 = DummyVersionable.builder()
        .validFrom(LocalDate.of(1999, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .build();

    List<DummyVersionable> versions = Arrays.asList(v1, v2);
    assertThat(DateValidations.areOverlapping(versions)).isTrue();
  }

  @Test
  void versionsAreNotOverlapping() {
    DummyVersionable v1 = DummyVersionable.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();

    DummyVersionable v2 = DummyVersionable.builder()
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .build();

    List<DummyVersionable> versions = Arrays.asList(v1, v2);
    assertThat(DateValidations.areOverlapping(versions)).isFalse();
  }

  @Test
  void versionsAreOverlappingMultiple() {
    DummyVersionable v1 = DummyVersionable.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();

    DummyVersionable v2 = DummyVersionable.builder()
        .validFrom(LocalDate.of(1999, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .build();
    DummyVersionable v3 = DummyVersionable.builder()
        .validFrom(LocalDate.of(1998, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .build();

    List<DummyVersionable> versions = Arrays.asList(v1, v2, v3);
    assertThat(DateValidations.areOverlapping(versions)).isTrue();
  }

  @Builder
  @Getter
  @Setter
  @AllArgsConstructor
  private static class DummyVersionable implements Versionable {

    private Long id;
    private LocalDate validFrom;
    private LocalDate validTo;
  }

}