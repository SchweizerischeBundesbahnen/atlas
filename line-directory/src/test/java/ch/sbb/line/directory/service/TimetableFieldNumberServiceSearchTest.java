package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion.TimetableFieldNumberVersionBuilder;
import ch.sbb.line.directory.model.search.TimetableFieldNumberSearchRestrictions;
import ch.sbb.line.directory.repository.TimetableFieldNumberVersionRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@IntegrationTest
 class TimetableFieldNumberServiceSearchTest {

  private static final LocalDate SEARCH_DATE = LocalDate.now();
  private static final LocalDate START_OF_MONTH_AT_SEARCH_DATE = SEARCH_DATE.withDayOfMonth(1);
  private static final LocalDate END_OF_MONTH_AT_SEARCH_DATE = SEARCH_DATE.withDayOfMonth(
      SEARCH_DATE.lengthOfMonth());

  private final TimetableFieldNumberVersionRepository versionRepository;
  private final List<TimetableFieldNumberVersion> versionList = new ArrayList<>();
  private final TimetableFieldNumberService timetableFieldNumberService;

  @Autowired
   TimetableFieldNumberServiceSearchTest(
      TimetableFieldNumberVersionRepository versionRepository,
      TimetableFieldNumberService timetableFieldNumberService) {
    this.versionRepository = versionRepository;
    this.timetableFieldNumberService = timetableFieldNumberService;
  }

  @BeforeEach
  void initialData() {
    TimetableFieldNumberVersionBuilder<?, ?> versionBuilder =
        TimetableFieldNumberVersion.builder()
            .ttfnid("ch:1:ttfnid:100000")
            .description("TimetableFieldNumberVersion 1")
            .swissTimetableFieldNumber("a.1")
            .status(Status.VALIDATED)
            .number("1.0")
            .comment("Valid this month")
            .validFrom(START_OF_MONTH_AT_SEARCH_DATE)
            .validTo(END_OF_MONTH_AT_SEARCH_DATE)
            .businessOrganisation("sbb");
    TimetableFieldNumberVersion version1 = versionBuilder.build();
    TimetableFieldNumberVersion version2 = versionBuilder.description(
            "TimetableFieldNumberVersion 2")
        .comment("Valid in future")
        .validFrom(
            START_OF_MONTH_AT_SEARCH_DATE.plusMonths(
                1))
        .validTo(
            START_OF_MONTH_AT_SEARCH_DATE.plusMonths(
                6))
        .build();
    TimetableFieldNumberVersion version3 = versionBuilder.ttfnid("ch:1:ttfnid:100001")
        .number("2.0")
        .description(
            "TimetableFieldNumberVersion 3")
        .swissTimetableFieldNumber("a.2")
        .comment("Valid this month")
        .validFrom(START_OF_MONTH_AT_SEARCH_DATE)
        .validTo(END_OF_MONTH_AT_SEARCH_DATE)
        .build();
    TimetableFieldNumberVersion version4 = versionBuilder.ttfnid("ch:1:ttfnid:100002")
        .description(
            "TimetableFieldNumberVersion 4")
        .swissTimetableFieldNumber("a.3")
        .build();
    TimetableFieldNumberVersion version5 = versionBuilder.ttfnid("ch:1:ttfnid:100003")
        .description(
            "TimetableFieldNumberVersion 5")
        .swissTimetableFieldNumber("a.1")
        .status(Status.IN_REVIEW)
        .build();
    versionList.addAll(List.of(version1, version2, version3, version4, version5));
    versionRepository.saveAll(versionList);
  }

  @Test
  void shouldSearch() {
    // Given initial data set
    // When
    List<TimetableFieldNumber> versionsSearched = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(Pageable.ofSize(5).withPage(0))
            .searchCriterias(List.of("1.0"))
            .build()).toList();
    // Then
    assertThat(versionsSearched).hasSize(1);
  }

  @Test
  void shouldFindVersionWithUnderscore() {
    // Given
    TimetableFieldNumberVersion versionWithUnderscore =
        TimetableFieldNumberVersion.builder()
            .ttfnid("ch:1:ttfnid:100011")
            .description("_bls")
            .swissTimetableFieldNumber("a.2")
            .comment("Valid this month")
            .status(Status.VALIDATED)
            .number("1.0")
            .validFrom(START_OF_MONTH_AT_SEARCH_DATE)
            .validTo(END_OF_MONTH_AT_SEARCH_DATE)
            .businessOrganisation("ch:1:sboid:23456789")
            .build();
    versionRepository.saveAndFlush(versionWithUnderscore);

    // When
    Page<TimetableFieldNumber> result = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .searchCriterias(List.of("_"))
            .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithMultipleUnderscore() {
    // Given
    TimetableFieldNumberVersion versionWithUnderscore =
        TimetableFieldNumberVersion.builder()
            .ttfnid("ch:1:ttfnid:100011")
            .description("__bls")
            .swissTimetableFieldNumber("a.2")
            .comment("Valid this month")
            .status(Status.VALIDATED)
            .number("1.0")
            .validFrom(START_OF_MONTH_AT_SEARCH_DATE)
            .validTo(END_OF_MONTH_AT_SEARCH_DATE)
            .businessOrganisation("ch:1:sboid:1235345")
            .build();
    versionRepository.saveAndFlush(versionWithUnderscore);

    // When
    Page<TimetableFieldNumber> result = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .searchCriterias(List.of("__"))
            .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithPercent() {
    // Given
    TimetableFieldNumberVersion versionWithUnderscore =
        TimetableFieldNumberVersion.builder()
            .ttfnid("ch:1:ttfnid:100011")
            .description("%bls")
            .swissTimetableFieldNumber("a.2")
            .comment("Valid this month")
            .status(Status.VALIDATED)
            .number("1.0")
            .comment("Valid this month")
            .validFrom(START_OF_MONTH_AT_SEARCH_DATE)
            .validTo(END_OF_MONTH_AT_SEARCH_DATE)
            .businessOrganisation("ch:1:sboid:2345245")
            .build();
    versionRepository.saveAndFlush(versionWithUnderscore);

    // When
    Page<TimetableFieldNumber> result = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .searchCriterias(List.of("%"))
            .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithMultiplePercente() {
    // Given
    TimetableFieldNumberVersion versionWithUnderscore =
        TimetableFieldNumberVersion.builder()
            .ttfnid("ch:1:ttfnid:100011")
            .description("%%bls")
            .swissTimetableFieldNumber("a.2")
            .comment("Valid this month")
            .status(Status.VALIDATED)
            .number("1.0")
            .comment("Valid this month")
            .validFrom(START_OF_MONTH_AT_SEARCH_DATE)
            .validTo(END_OF_MONTH_AT_SEARCH_DATE)
            .businessOrganisation("ch:1:sboid:36456154")
            .build();
    versionRepository.saveAndFlush(versionWithUnderscore);

    // When
    Page<TimetableFieldNumber> result = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .searchCriterias(List.of("%%"))
            .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void searchWithOneCriteriaAndNoValidOn() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(Pageable.ofSize(20).withPage(0))
            .searchCriterias(List.of("version 3"))
            .build()).toList();
    // Then
    assertThat(searchResult).hasSize(1);
    assertThat(searchResult).first().usingRecursiveComparison().isEqualTo(versionList.get(2));
  }

  @Test
  void searchWithOneCriteriaAndWithValidOn() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(

                Pageable.ofSize(20).withPage(0))
            .searchCriterias(List.of("version"))
            .validOn(Optional.of(SEARCH_DATE))
            .build()).toList();
    // Then
    assertThat(searchResult).hasSize(4);
    assertThat(searchResult.get(0).getTtfnid()).isEqualTo(versionList.get(0).getTtfnid());
  }

  @Test
  void searchWithoutCriteriaAndWithValidOn() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(
                PageRequest.of(0, 20, Sort.by("description")))
            .validOn(Optional.of(SEARCH_DATE))
            .build()).toList();
    // Then
    assertThat(searchResult).hasSize(4);
    assertThat(searchResult.get(0).getTtfnid()).isEqualTo(versionList.get(0).getTtfnid());
    assertThat(searchResult.get(1)).usingRecursiveComparison().isEqualTo(versionList.get(2));
    assertThat(searchResult.get(2)).usingRecursiveComparison().isEqualTo(versionList.get(3));
    assertThat(searchResult.get(3)).usingRecursiveComparison().isEqualTo(versionList.get(4));
  }

  @Test
  void searchWithMultipleCriteriasWithoutValidOn() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(Pageable.ofSize(20).withPage(0))
            .searchCriterias(List.of("a.1", "version 5"))
            .build()).toList();
    // Then
    assertThat(searchResult).hasSize(1);
    assertThat(searchResult).first().usingRecursiveComparison().isEqualTo(versionList.get(4));
  }

  @Test
  void searchWithCriteriWithValidOnWithStatusChoices() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(
                PageRequest.of(0, 20, Sort.by("description")))
            .searchCriterias(
                List.of("TimetableFieldNumberVersion"))
            .validOn(Optional.of(SEARCH_DATE))
            .statusRestrictions(List.of(Status.VALIDATED))
            .build()).toList();
    // Then
    assertThat(searchResult).hasSize(3);
    assertThat(searchResult.get(0).getTtfnid()).isEqualTo(versionList.get(0).getTtfnid());
    assertThat(searchResult.get(1)).usingRecursiveComparison().isEqualTo(versionList.get(2));
    assertThat(searchResult.get(2)).usingRecursiveComparison().isEqualTo(versionList.get(3));
  }

  @Test
  void searchWithCriteriaWithStatusAndWithoutValidOn() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(Pageable.ofSize(20).withPage(0))
            .searchCriterias(List.of("ch:1:ttfnid:100000"))
            .statusRestrictions(List.of(Status.IN_REVIEW))
            .build()).toList();
    // Then
    assertThat(searchResult).isEmpty();

    searchResult = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(Pageable.ofSize(20).withPage(0))
            .searchCriterias(List.of("ch:1:ttfnid:100003"))
            .statusRestrictions(List.of(Status.IN_REVIEW))
            .build()).toList();
    assertThat(searchResult).hasSize(1);
    assertThat(searchResult).first().usingRecursiveComparison().isEqualTo(versionList.get(4));
  }

  @Test
  void searchWithCriteriaWithStatusAndWithValidOn() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(Pageable.ofSize(5).withPage(0))
            .searchCriterias(List.of("a.1"))
            .validOn(Optional.of(SEARCH_DATE))
            .statusRestrictions(List.of(Status.VALIDATED))
            .build()).toList();
    // Then
    assertThat(searchResult).hasSize(1);
    assertThat(searchResult.get(0).getTtfnid()).isEqualTo(versionList.get(0).getTtfnid());
  }

  @Test
  void searchWithPageNumber0AndPageSize3() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(
                PageRequest.of(0, 3, Sort.by("description")))
            .searchCriterias(List.of("version"))
            .build()).toList();
    // Then
    assertThat(searchResult).hasSize(3);
    assertThat(searchResult.get(0).getTtfnid()).isEqualTo(versionList.get(0).getTtfnid());
    assertThat(searchResult.get(1)).usingRecursiveComparison().isEqualTo(versionList.get(2));
    assertThat(searchResult.get(2)).usingRecursiveComparison().isEqualTo(versionList.get(3));
  }

  @Test
  void searchWithPageNumber1AndPageSize2() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(
                PageRequest.of(1, 2, Sort.by("description")))
            .searchCriterias(List.of("version"))
            .build()).toList();
    // Then
    assertThat(searchResult).hasSize(2);
    assertThat(searchResult.get(0)).usingRecursiveComparison().isEqualTo(versionList.get(3));
    assertThat(searchResult.get(1)).usingRecursiveComparison().isEqualTo(versionList.get(4));
  }

  @Test
  void searchWithSortingDescriptionDesc() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(PageRequest.of(0, 10,
                Sort.by(Direction.DESC, "description")))
            .searchCriterias(List.of("a.1"))
            .build()).toList();
    // Then
    assertThat(searchResult).hasSize(2);
    assertThat(searchResult.get(0)).usingRecursiveComparison().isEqualTo(versionList.get(4));
    assertThat(searchResult.get(1).getTtfnid()).isEqualTo(versionList.get(0).getTtfnid());
  }

  @Test
  void searchWithSortingSwissTimetableFieldNumberAndTtfnidAsc() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(PageRequest.of(0, 10, Sort.by(Direction.ASC,
                "swissTimetableFieldNumber", "ttfnid")))
            .build()).toList();
    // Then
    assertThat(searchResult).hasSize(4);
    assertThat(searchResult.get(0).getTtfnid()).isEqualTo(versionList.get(0).getTtfnid());
    assertThat(searchResult.get(1)).usingRecursiveComparison().isEqualTo(versionList.get(4));
    assertThat(searchResult.get(2)).usingRecursiveComparison().isEqualTo(versionList.get(2));
    assertThat(searchResult.get(3)).usingRecursiveComparison().isEqualTo(versionList.get(3));
  }

  @Test
  void searchVersionsWithValidAtAndTtfnids() {
    // Given initial dataset
    // When
    List<TimetableFieldNumberVersion> result =
        timetableFieldNumberService.getVersionsValidAt(Set.of(versionList.get(0).getTtfnid()), SEARCH_DATE);
    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getTtfnid()).isEqualTo(versionList.get(0).getTtfnid());
  }

  @AfterEach
  void cleanup() {
    versionRepository.deleteAll();
    versionList.clear();
  }

}
