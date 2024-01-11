package ch.sbb.atlas.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.versioning.model.Versionable;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

class OverviewServiceTest {

  @Test
  void shouldMergeVersions() {
    DummyVersionable version1 = DummyVersionable.builder()
        .id(1L)
        .sloid("ch:1:sloid:7000")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    DummyVersionable version2 = DummyVersionable.builder()
        .id(2L)
        .sloid("ch:1:sloid:7000")
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();

    List<DummyVersionable> mergedVersions = OverviewService.mergeVersionsForDisplay(List.of(version1, version2),
        (x, y) -> x.getSloid().equals(y.getSloid()));
    assertThat(mergedVersions).hasSize(1);
    assertThat(mergedVersions.get(0).getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(mergedVersions.get(0).getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(mergedVersions.get(0).getId()).isEqualTo(2);
  }

  @Test
  void shouldMergeVersionsAndShowVersionValidToday() {
    DummyVersionable version1 = DummyVersionable.builder()
        .id(1L)
        .sloid("ch:1:sloid:7000")
        .validFrom(LocalDate.now().minusDays(1))
        .validTo(LocalDate.now().plusDays(1))
        .build();
    DummyVersionable version2 = DummyVersionable.builder()
        .id(2L)
        .sloid("ch:1:sloid:7000")
        .validFrom(LocalDate.now().plusDays(2))
        .validTo(LocalDate.now().plusDays(20))
        .build();

    List<DummyVersionable> mergedVersions = OverviewService.mergeVersionsForDisplay(List.of(version1, version2),
        (x, y) -> x.getSloid().equals(y.getSloid()));
    assertThat(mergedVersions).hasSize(1);
    assertThat(mergedVersions.get(0).getId()).isEqualTo(1);

    Container<DummyVersionable> pagedContainer = OverviewService.toPagedContainer(mergedVersions, Pageable.ofSize(1));
    assertThat(pagedContainer.getTotalCount()).isEqualTo(1);
  }

  @Builder
  @Getter
  @Setter
  @AllArgsConstructor
  private static class DummyVersionable implements Versionable {

    private Long id;
    private String sloid;
    private LocalDate validFrom;
    private LocalDate validTo;
  }

}