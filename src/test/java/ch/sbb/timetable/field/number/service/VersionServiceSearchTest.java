package ch.sbb.timetable.field.number.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.timetable.field.number.IntegrationTest;
import ch.sbb.timetable.field.number.entity.TimetableFieldNumber;
import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.enumaration.Status;
import ch.sbb.timetable.field.number.repository.VersionRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

@IntegrationTest
public class VersionServiceSearchTest {

  private final VersionRepository versionRepository;
  private final VersionService versionService;
  private final Version.VersionBuilder versionBuilder = Version.builder()
      .ttfnid("ch:1:ttfnid:100000")
      .name("Version 1")
      .swissTimetableFieldNumber("a.1")
      .status(Status.ACTIVE)
      .number("1.0")
      .validFrom(LocalDate.of(2021, 12, 1))
      .validTo(LocalDate.of(2021, 12, 31))
      .businessOrganisation("sbb");
  private final Version version1 = versionBuilder.build();
  private final Version version2 = versionBuilder.ttfnid("ch:1:ttfnid:100001").name("Version 2").build();

  @Autowired
  public VersionServiceSearchTest(VersionRepository versionRepository, VersionService versionService) {
    this.versionRepository = versionRepository;
    this.versionService = versionService;
  }

  @BeforeEach
  void initialData() {
    versionRepository.saveAll(List.of(version1, version2));
  }

  @Test
  void shouldSearch() {
    // Given initial data set
    // When
    List<TimetableFieldNumber> versionsSearched = versionService.getVersionsSearched(
            Pageable.ofSize(5).withPage(0),
            List.of("2"),
            null,
            null
        )
        .toList();
    // Then
    assertThat(versionsSearched).hasSize(1);
    assertThat(versionsSearched).first().usingRecursiveComparison().isEqualTo(version2);
  }

  @AfterEach
  void cleanup() {
    versionRepository.deleteAll();
  }

}
