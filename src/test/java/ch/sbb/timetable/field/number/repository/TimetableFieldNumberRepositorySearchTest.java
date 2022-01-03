package ch.sbb.timetable.field.number.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.timetable.field.number.IntegrationTest;
import ch.sbb.timetable.field.number.entity.TimetableFieldNumber;
import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.entity.Version.VersionBuilder;
import ch.sbb.timetable.field.number.enumaration.Status;
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

  private final VersionRepository versionRepository;
  private final TimetableFieldNumberRepository timetableFieldNumberRepository;
  private final List<Version> versionList = new ArrayList<>();

  @Autowired
  public TimetableFieldNumberRepositorySearchTest(
      TimetableFieldNumberRepository timetableFieldNumberRepository,
      VersionRepository versionRepository) {
    this.versionRepository = versionRepository;
    this.timetableFieldNumberRepository = timetableFieldNumberRepository;
  }

  @BeforeEach
  void initialData() {
    VersionBuilder versionBuilder = Version.builder()
                                           .ttfnid("ch:1:ttfnid:100000")
                                           .name("Version 1")
                                           .swissTimetableFieldNumber("a.1")
                                           .status(Status.ACTIVE)
                                           .number("1.0")
                                           .comment("Valid this month")
                                           .validFrom(START_OF_MONTH_AT_SEARCH_DATE)
                                           .validTo(END_OF_MONTH_AT_SEARCH_DATE)
                                           .businessOrganisation("sbb");
    Version version1 = versionBuilder.build();
    Version version2 = versionBuilder.name("Version 2")
                                     .comment("Valid in future")
                                     .validFrom(START_OF_MONTH_AT_SEARCH_DATE.plusMonths(1))
                                     .validTo(START_OF_MONTH_AT_SEARCH_DATE.plusMonths(6))
                                     .build();
    Version version3 = versionBuilder.ttfnid("ch:1:ttfnid:100001")
                                     .name("Version 3")
                                     .swissTimetableFieldNumber("a.2")
                                     .comment("Valid this month")
                                     .validFrom(START_OF_MONTH_AT_SEARCH_DATE)
                                     .validTo(END_OF_MONTH_AT_SEARCH_DATE)
                                     .build();
    Version version4 = versionBuilder.ttfnid("ch:1:ttfnid:100002")
                                     .name("Version 4")
                                     .swissTimetableFieldNumber("a.3")
                                     .build();
    Version version5 = versionBuilder.ttfnid("ch:1:ttfnid:100003")
                                     .name("Version 5")
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
    assertThat(searchResult.get(0).getName()).isEqualTo(versionList.get(0).getName());
  }

  @Test
  void searchWithoutCriteriaAndWithValidOn() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberRepository.searchVersions(
                                                                                PageRequest.of(0, 20, Sort.by("name")),
                                                                                null,
                                                                                SEARCH_DATE,
                                                                                null
                                                                            )
                                                                            .toList();
    // Then
    assertThat(searchResult.size()).isEqualTo(4);
    assertThat(searchResult.get(0).getName()).isEqualTo(versionList.get(0).getName());
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
                                                                                PageRequest.of(0, 20, Sort.by("name")),
                                                                                List.of("Version"),
                                                                                SEARCH_DATE,
                                                                                List.of(Status.ACTIVE)
                                                                            )
                                                                            .toList();
    // Then
    assertThat(searchResult.size()).isEqualTo(3);
    assertThat(searchResult.get(0).getName()).isEqualTo(versionList.get(0).getName());
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
    assertThat(searchResult.get(0).getName()).isEqualTo(versionList.get(0).getName());
  }

  @Test
  void searchWithPageNumber0AndPageSize3() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberRepository.searchVersions(
                                                                                PageRequest.of(0, 3, Sort.by("name")),
                                                                                List.of("version"),
                                                                                null,
                                                                                null
                                                                            )
                                                                            .toList();
    // Then
    assertThat(searchResult.size()).isEqualTo(3);
    assertThat(searchResult.get(0).getName()).isEqualTo(versionList.get(0).getName());
    assertThat(searchResult.get(1)).usingRecursiveComparison().isEqualTo(versionList.get(2));
    assertThat(searchResult.get(2)).usingRecursiveComparison().isEqualTo(versionList.get(3));
  }

  @Test
  void searchWithPageNumber1AndPageSize2() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberRepository.searchVersions(
                                                                                PageRequest.of(1, 2, Sort.by("name")),
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
  void searchWithSortingNameDesc() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberRepository.searchVersions(
                                                                                PageRequest.of(0, 10, Sort.by(Direction.DESC, "name")),
                                                                                List.of("a.1"),
                                                                                null,
                                                                                null
                                                                            )
                                                                            .toList();
    // Then
    assertThat(searchResult.size()).isEqualTo(2);
    assertThat(searchResult.get(0)).usingRecursiveComparison().isEqualTo(versionList.get(4));
    assertThat(searchResult.get(1).getName()).isEqualTo(versionList.get(0).getName());
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
    assertThat(searchResult.get(0).getName()).isEqualTo(versionList.get(0).getName());
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
