package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.line.directory.IntegrationTest;
import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.repository.TimetableFieldNumberVersionRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

@IntegrationTest
public class TimetableFieldNumberServiceSearchTest {

  private final TimetableFieldNumberVersionRepository versionRepository;
  private final TimetableFieldNumberService timetableFieldNumberService;
  private final TimetableFieldNumberVersion.TimetableFieldNumberVersionBuilder versionBuilder = TimetableFieldNumberVersion.builder()
                                                                                                       .ttfnid("ch:1:ttfnid:100000")
                                                                                                       .description("TimetableFieldNumberVersion 1")
                                                                                                       .swissTimetableFieldNumber("a.1")
                                                                                                       .status(Status.ACTIVE)
                                                                                                       .number("1.0")
                                                                                                       .validFrom(LocalDate.of(2021, 12, 1))
                                                                                                       .validTo(LocalDate.of(2021, 12, 31))
                                                                                                       .businessOrganisation("sbb");
  private final TimetableFieldNumberVersion version1 = versionBuilder.build();
  private final TimetableFieldNumberVersion version2 = versionBuilder.ttfnid("ch:1:ttfnid:100001").description("TimetableFieldNumberVersion 2").build();

  @Autowired
  public TimetableFieldNumberServiceSearchTest(
      TimetableFieldNumberVersionRepository versionRepository, TimetableFieldNumberService timetableFieldNumberService) {
    this.versionRepository = versionRepository;
    this.timetableFieldNumberService = timetableFieldNumberService;
  }

  @BeforeEach
  void initialData() {
    versionRepository.saveAll(List.of(version1, version2));
  }

  @Test
  void shouldSearch() {
    // Given initial data set
    // When
    List<TimetableFieldNumber> versionsSearched = timetableFieldNumberService.getVersionsSearched(
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
