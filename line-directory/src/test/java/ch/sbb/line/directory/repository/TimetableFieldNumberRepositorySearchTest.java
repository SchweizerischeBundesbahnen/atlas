package ch.sbb.line.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.IntegrationTest;
import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion.TimetableFieldNumberVersionBuilder;
import ch.sbb.atlas.model.Status;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@IntegrationTest
public class TimetableFieldNumberRepositorySearchTest {

  private static final LocalDate SEARCH_DATE = LocalDate.now();
  private static final LocalDate START_OF_MONTH_AT_SEARCH_DATE = SEARCH_DATE.withDayOfMonth(1);
  private static final LocalDate END_OF_MONTH_AT_SEARCH_DATE = SEARCH_DATE
      .withDayOfMonth(
          SEARCH_DATE.lengthOfMonth());

  private final TimetableFieldNumberVersionRepository versionRepository;
  private final TimetableFieldNumberRepository timetableFieldNumberRepository;
  private final List<TimetableFieldNumberVersion> versionList = new ArrayList<>();

  @Autowired
  public TimetableFieldNumberRepositorySearchTest(
      TimetableFieldNumberRepository timetableFieldNumberRepository,
      TimetableFieldNumberVersionRepository versionRepository) {
    this.versionRepository = versionRepository;
    this.timetableFieldNumberRepository = timetableFieldNumberRepository;
  }

  @BeforeEach
  void initialData() {
    TimetableFieldNumberVersionBuilder versionBuilder = TimetableFieldNumberVersion.builder()
                                                                                                               .ttfnid(
                                                                                                                   "ch:1:ttfnid:100000")
                                                                                                               .description(
                                                                                                                   "TimetableFieldNumberVersion 1")
                                                                                                               .swissTimetableFieldNumber(
                                                                                                                   "a.1")
                                                                                                               .status(
                                                                                                                   Status.ACTIVE)
                                                                                                               .number(
                                                                                                                   "1.0")
                                                                                                               .comment(
                                                                                                                   "Valid this month")
                                                                                                               .validFrom(
                                                                                                                   START_OF_MONTH_AT_SEARCH_DATE)
                                                                                                               .validTo(
                                                                                                                   END_OF_MONTH_AT_SEARCH_DATE)
                                                                                                               .businessOrganisation(
                                                                                                                   "sbb");
    TimetableFieldNumberVersion version1 = versionBuilder.build();
    TimetableFieldNumberVersion version2 = versionBuilder.description("TimetableFieldNumberVersion 2")
                                                         .comment("Valid in future")
                                                         .validFrom(START_OF_MONTH_AT_SEARCH_DATE.plusMonths(1))
                                                         .validTo(START_OF_MONTH_AT_SEARCH_DATE.plusMonths(6))
                                                         .build();
    TimetableFieldNumberVersion version3 = versionBuilder.ttfnid("ch:1:ttfnid:100001")
                                                         .description("TimetableFieldNumberVersion 3")
                                                         .swissTimetableFieldNumber("a.2")
                                                         .comment("Valid this month")
                                                         .validFrom(START_OF_MONTH_AT_SEARCH_DATE)
                                                         .validTo(END_OF_MONTH_AT_SEARCH_DATE)
                                                         .build();
    TimetableFieldNumberVersion version4 = versionBuilder.ttfnid("ch:1:ttfnid:100002")
                                                         .description("TimetableFieldNumberVersion 4")
                                                         .swissTimetableFieldNumber("a.3")
                                                         .build();
    TimetableFieldNumberVersion version5 = versionBuilder.ttfnid("ch:1:ttfnid:100003")
                                                         .description("TimetableFieldNumberVersion 5")
                                                         .swissTimetableFieldNumber("a.1")
                                                         .status(Status.IN_REVIEW)
                                                         .build();
    versionList.addAll(List.of(version1, version2, version3, version4, version5));
    versionRepository.saveAll(versionList);
  }

  @Test
  void searchWithOneCriteriaAndNoValidOn() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberRepository.searchVersions(
                                                                                Pageable.ofSize(20).withPage(0),
                                                                                List.of("version 3"),
                                                                                null,
                                                                                null
                                                                            )
                                                                            .toList();
    // Then
    assertThat(searchResult.size()).isEqualTo(1);
    assertThat(searchResult).first().usingRecursiveComparison().isEqualTo(versionList.get(2));
  }

  @Test
  void searchWithOneCriteriaAndWithValidOn() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberRepository.searchVersions(
                                                                                Pageable.ofSize(20).withPage(0),
                                                                                List.of("version"),
                                                                                SEARCH_DATE,
                                                                                null
                                                                            )
                                                                            .toList();
    // Then
    assertThat(searchResult.size()).isEqualTo(4);
    assertThat(searchResult.get(0).getTtfnid()).isEqualTo(versionList.get(0).getTtfnid());
  }

  @Test
  void searchWithoutCriteriaAndWithValidOn() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberRepository.searchVersions(
                                                                                PageRequest.of(0, 20, Sort.by("description")),
                                                                                null,
                                                                                SEARCH_DATE,
                                                                                null
                                                                            )
                                                                            .toList();
    // Then
    assertThat(searchResult.size()).isEqualTo(4);
    assertThat(searchResult.get(0).getTtfnid()).isEqualTo(versionList.get(0).getTtfnid());
    assertThat(searchResult.get(1)).usingRecursiveComparison().isEqualTo(versionList.get(2));
    assertThat(searchResult.get(2)).usingRecursiveComparison().isEqualTo(versionList.get(3));
    assertThat(searchResult.get(3)).usingRecursiveComparison().isEqualTo(versionList.get(4));
  }

  @Test
  void searchWithMultipleCriteriasWithoutValidOn() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberRepository.searchVersions(
                                                                                Pageable.ofSize(20).withPage(0),
                                                                                List.of("a.1", "version 5"),
                                                                                null,
                                                                                null
                                                                            )
                                                                            .toList();
    // Then
    assertThat(searchResult.size()).isEqualTo(1);
    assertThat(searchResult).first().usingRecursiveComparison().isEqualTo(versionList.get(4));
  }

  @Test
  void searchWithCriteriWithValidOnWithStatusChoices() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberRepository.searchVersions(
                                                                                PageRequest.of(0, 20, Sort.by("description")),
                                                                                List.of("TimetableFieldNumberVersion"),
                                                                                SEARCH_DATE,
                                                                                List.of(Status.ACTIVE)
                                                                            )
                                                                            .toList();
    // Then
    assertThat(searchResult.size()).isEqualTo(3);
    assertThat(searchResult.get(0).getTtfnid()).isEqualTo(versionList.get(0).getTtfnid());
    assertThat(searchResult.get(1)).usingRecursiveComparison().isEqualTo(versionList.get(2));
    assertThat(searchResult.get(2)).usingRecursiveComparison().isEqualTo(versionList.get(3));
  }

  @Test
  void searchWithCriteriaWithStatusAndWithoutValidOn() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberRepository.searchVersions(
                                                                                Pageable.ofSize(20).withPage(0),
                                                                                List.of("ch:1:ttfnid:100000"),
                                                                                null,
                                                                                List.of(Status.IN_REVIEW)
                                                                            )
                                                                            .toList();
    // Then
    assertThat(searchResult.size()).isZero();

    searchResult = timetableFieldNumberRepository.searchVersions(
                                                     Pageable.ofSize(20).withPage(0),
                                                     List.of("ch:1:ttfnid:100003"),
                                                     null,
                                                     List.of(Status.IN_REVIEW)
                                                 )
                                                 .toList();
    assertThat(searchResult.size()).isEqualTo(1);
    assertThat(searchResult).first().usingRecursiveComparison().isEqualTo(versionList.get(4));
  }

  @Test
  void searchWithCriteriaWithStatusAndWithValidOn() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberRepository.searchVersions(
                                                                                Pageable.ofSize(5).withPage(0),
                                                                                List.of("a.1"),
                                                                                SEARCH_DATE,
                                                                                List.of(Status.ACTIVE)
                                                                            )
                                                                            .toList();
    // Then
    assertThat(searchResult.size()).isEqualTo(1);
    assertThat(searchResult.get(0).getTtfnid()).isEqualTo(versionList.get(0).getTtfnid());
  }

  @Test
  void searchWithPageNumber0AndPageSize3() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberRepository.searchVersions(
                                                                                PageRequest.of(0, 3, Sort.by("description")),
                                                                                List.of("version"),
                                                                                null,
                                                                                null
                                                                            )
                                                                            .toList();
    // Then
    assertThat(searchResult.size()).isEqualTo(3);
    assertThat(searchResult.get(0).getTtfnid()).isEqualTo(versionList.get(0).getTtfnid());
    assertThat(searchResult.get(1)).usingRecursiveComparison().isEqualTo(versionList.get(2));
    assertThat(searchResult.get(2)).usingRecursiveComparison().isEqualTo(versionList.get(3));
  }

  @Test
  void searchWithPageNumber1AndPageSize2() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberRepository.searchVersions(
                                                                                PageRequest.of(1, 2, Sort.by("description")),
                                                                                List.of("version"),
                                                                                null,
                                                                                null
                                                                            )
                                                                            .toList();
    // Then
    assertThat(searchResult.size()).isEqualTo(2);
    assertThat(searchResult.get(0)).usingRecursiveComparison().isEqualTo(versionList.get(3));
    assertThat(searchResult.get(1)).usingRecursiveComparison().isEqualTo(versionList.get(4));
  }

  @Test
  void searchWithSortingDescriptionDesc() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberRepository.searchVersions(
                                                                                PageRequest.of(0, 10, Sort.by(Direction.DESC, "description")),
                                                                                List.of("a.1"),
                                                                                null,
                                                                                null
                                                                            )
                                                                            .toList();
    // Then
    assertThat(searchResult.size()).isEqualTo(2);
    assertThat(searchResult.get(0)).usingRecursiveComparison().isEqualTo(versionList.get(4));
    assertThat(searchResult.get(1).getTtfnid()).isEqualTo(versionList.get(0).getTtfnid());
  }

  @Test
  void searchWithSortingSwissTimetableFieldNumberAndTtfnidAsc() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberRepository.searchVersions(
                                                                                PageRequest.of(0, 10, Sort.by(Direction.ASC, "swissTimetableFieldNumber", "ttfnid")),
                                                                                null,
                                                                                null,
                                                                                null
                                                                            )
                                                                            .toList();
    // Then
    assertThat(searchResult.size()).isEqualTo(4);
    assertThat(searchResult.get(0).getTtfnid()).isEqualTo(versionList.get(0).getTtfnid());
    assertThat(searchResult.get(1)).usingRecursiveComparison().isEqualTo(versionList.get(4));
    assertThat(searchResult.get(2)).usingRecursiveComparison().isEqualTo(versionList.get(2));
    assertThat(searchResult.get(3)).usingRecursiveComparison().isEqualTo(versionList.get(3));
  }

  @AfterEach
  void cleanup() {
    versionRepository.deleteAll();
    versionList.clear();
  }

}
