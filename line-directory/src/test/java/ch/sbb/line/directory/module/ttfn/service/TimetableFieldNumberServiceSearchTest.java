package ch.sbb.line.directory.module.ttfn.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.enumaration.TtfnMeanOfTransport;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.module.ttfn.entity.TimetableFieldNumber;
import ch.sbb.line.directory.module.ttfn.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.module.ttfn.entity.TimetableFieldNumberVersion.TimetableFieldNumberVersionBuilder;
import ch.sbb.line.directory.module.ttfn.repository.TimetableFieldNumberVersionRepository;
import ch.sbb.line.directory.module.ttfn.search.TimetableFieldNumberSearchRestrictions;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
  private static final LocalDate END_OF_MONTH_AT_SEARCH_DATE = SEARCH_DATE.withDayOfMonth(SEARCH_DATE.lengthOfMonth());
  private static final String BUCHSATZTITEL = "Buchsatztitel Grenchen Nord";

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

  private static TimetableFieldNumberVersionBuilder<?, ?> versionBuilder() {
    return TimetableFieldNumberVersion.builder()
        .ttfnid("ch:1:ttfnid:100000")
        .descriptionOutwardLine1("TimetableFieldNumberVersion 1")
        .meanOfTransport(TtfnMeanOfTransport.TRAIN)
        .status(Status.VALIDATED)
        .number("1.0")
        .validFrom(START_OF_MONTH_AT_SEARCH_DATE)
        .validTo(END_OF_MONTH_AT_SEARCH_DATE)
        .businessOrganisation("sbb");
  }

  private static Stream<Arguments> searchableDescriptionLines() {
    return Stream.of(
        Arguments.of(TimetableFieldNumberVersion.Fields.descriptionOutwardLine1,
            (Consumer<TimetableFieldNumberVersion>) version -> version.setDescriptionOutwardLine1(BUCHSATZTITEL)),
        Arguments.of(TimetableFieldNumberVersion.Fields.descriptionOutwardLine2,
            (Consumer<TimetableFieldNumberVersion>) version -> version.setDescriptionOutwardLine2(BUCHSATZTITEL)),
        Arguments.of(TimetableFieldNumberVersion.Fields.descriptionOutwardLine3,
            (Consumer<TimetableFieldNumberVersion>) version -> version.setDescriptionOutwardLine3(BUCHSATZTITEL)),
        Arguments.of(TimetableFieldNumberVersion.Fields.descriptionReturnLine1,
            (Consumer<TimetableFieldNumberVersion>) version -> version.setDescriptionReturnLine1(BUCHSATZTITEL)),
        Arguments.of(TimetableFieldNumberVersion.Fields.descriptionReturnLine2,
            (Consumer<TimetableFieldNumberVersion>) version -> version.setDescriptionReturnLine2(BUCHSATZTITEL)),
        Arguments.of(TimetableFieldNumberVersion.Fields.descriptionReturnLine3,
            (Consumer<TimetableFieldNumberVersion>) version -> version.setDescriptionReturnLine3(BUCHSATZTITEL)));
  }

  @BeforeEach
  void initialData() {
    TimetableFieldNumberVersion version1 = versionBuilder()
        .status(Status.IN_REVIEW)
        .build();
    TimetableFieldNumberVersion version2 = versionBuilder()
        .descriptionOutwardLine1("TimetableFieldNumberVersion 2")
        .validFrom(START_OF_MONTH_AT_SEARCH_DATE.plusMonths(1))
        .validTo(START_OF_MONTH_AT_SEARCH_DATE.plusMonths(6))
        .build();
    TimetableFieldNumberVersion version3 = versionBuilder()
        .ttfnid("ch:1:ttfnid:100001")
        .number("3.0")
        .descriptionOutwardLine1("TimetableFieldNumberVersion 3")
        .build();
    TimetableFieldNumberVersion version4 = versionBuilder()
        .ttfnid("ch:1:ttfnid:100002")
        .number("4.0")
        .descriptionOutwardLine1("TimetableFieldNumberVersion 4")
        .build();
    TimetableFieldNumberVersion version5 = versionBuilder()
        .ttfnid("ch:1:ttfnid:100003")
        .number("5.0")
        .descriptionOutwardLine1("TimetableFieldNumberVersion 5")
        .status(Status.IN_REVIEW)
        .build();
    versionList.addAll(List.of(version1, version2, version3, version4, version5));
    versionRepository.saveAll(versionList);
  }

  @AfterEach
  void cleanup() {
    versionRepository.deleteAll();
    versionList.clear();
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
            .descriptionOutwardLine1("_bls")
            .descriptionReturnLine1("_bls")
            .meanOfTransport(TtfnMeanOfTransport.TRAIN)
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
            .descriptionOutwardLine1("__bls")
            .descriptionReturnLine1("__bls")
            .meanOfTransport(TtfnMeanOfTransport.TRAIN)
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
            .descriptionOutwardLine1("%bls")
            .descriptionReturnLine1("%bls")
            .meanOfTransport(TtfnMeanOfTransport.TRAIN)
            .status(Status.VALIDATED)
            .number("1.0")
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
  void shouldFindVersionWithMultiplePercents() {
    // Given
    TimetableFieldNumberVersion versionWithUnderscore =
        TimetableFieldNumberVersion.builder()
            .ttfnid("ch:1:ttfnid:100011")
            .descriptionOutwardLine1("%%bls")
            .descriptionReturnLine1("%%bls")
            .meanOfTransport(TtfnMeanOfTransport.TRAIN)
            .status(Status.VALIDATED)
            .number("1.0")
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
            .pageable(Pageable.ofSize(20).withPage(0))
            .searchCriterias(List.of("version"))
            .validOn(SEARCH_DATE)
            .build()).toList();
    // Then
    assertThat(searchResult).hasSize(4);
    assertThat(searchResult.getFirst().getTtfnid()).isEqualTo(versionList.getFirst().getTtfnid());
  }

  @Test
  void searchWithoutCriteriaAndWithValidOn() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(PageRequest.of(0, 20, Sort.by("descriptionOutwardLine1")))
            .validOn(SEARCH_DATE)
            .build()).toList();
    // Then
    assertThat(searchResult).hasSize(4);
    assertThat(searchResult.getFirst().getTtfnid()).isEqualTo(versionList.getFirst().getTtfnid());
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
            .searchCriterias(List.of("5.0", "version 5"))
            .build()).toList();
    // Then
    assertThat(searchResult).hasSize(1);
    assertThat(searchResult).first().usingRecursiveComparison().isEqualTo(versionList.get(4));
  }

  @Test
  void searchWithCriteriaWithValidOnWithStatusChoices() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(PageRequest.of(0, 20, Sort.by("descriptionOutwardLine1")))
            .searchCriterias(List.of("TimetableFieldNumberVersion"))
            .validOn(SEARCH_DATE)
            .statusRestrictions(List.of(Status.VALIDATED))
            .build()).toList();
    // Then
    assertThat(searchResult).hasSize(2);
    assertThat(searchResult).first().usingRecursiveComparison().isEqualTo(versionList.get(2));
    assertThat(searchResult.get(1)).usingRecursiveComparison().isEqualTo(versionList.get(3));
  }

  @Test
  void searchWithCriteriaWithStatusAndWithoutValidOn() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(Pageable.ofSize(20).withPage(0))
            .searchCriterias(List.of("TimetableFieldNumberVersion 4"))
            .statusRestrictions(List.of(Status.IN_REVIEW))
            .build()).toList();
    // Then
    assertThat(searchResult).isEmpty();

    searchResult = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(Pageable.ofSize(20).withPage(0))
            .searchCriterias(List.of("TimetableFieldNumberVersion 5"))
            .statusRestrictions(List.of(Status.IN_REVIEW))
            .build()).toList();
    assertThat(searchResult).hasSize(1);
    assertThat(searchResult).first().usingRecursiveComparison().isEqualTo(versionList.get(4));
  }

  @ParameterizedTest
  @MethodSource("searchableDescriptionLines")
  void shouldFindVersionByEveryDescriptionLine(String descriptionLine,
      Consumer<TimetableFieldNumberVersion> descriptionLineSetter) {
    // Given
    TimetableFieldNumberVersion version = versionBuilder()
        .ttfnid("ch:1:ttfnid:100020")
        .number("20.0")
        .descriptionOutwardLine1(null)
        .build();
    descriptionLineSetter.accept(version);
    versionRepository.saveAndFlush(version);

    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .searchCriterias(List.of(BUCHSATZTITEL))
            .build()).toList();

    // Then
    assertThat(searchResult).as("search on %s", descriptionLine).hasSize(1);
    assertThat(searchResult.getFirst().getTtfnid()).isEqualTo("ch:1:ttfnid:100020");
  }

  @Test
  void shouldNotFindVersionByTtfnid() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(Pageable.ofSize(20).withPage(0))
            .searchCriterias(List.of("ch:1:ttfnid:100001"))
            .build()).toList();
    // Then
    assertThat(searchResult).isEmpty();
  }

  @Test
  void searchWithCriteriaWithStatusAndWithValidOn() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(Pageable.ofSize(5).withPage(0))
            .searchCriterias(List.of("3.0"))
            .validOn(SEARCH_DATE)
            .statusRestrictions(List.of(Status.VALIDATED))
            .build()).toList();
    // Then
    assertThat(searchResult).hasSize(1);
    assertThat(searchResult).first().usingRecursiveComparison().isEqualTo(versionList.get(2));
  }

  @Test
  void searchWithPageNumber0AndPageSize3() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(PageRequest.of(0, 3, Sort.by("descriptionOutwardLine1")))
            .searchCriterias(List.of("version"))
            .build()).toList();
    // Then
    assertThat(searchResult).hasSize(3);
    assertThat(searchResult.getFirst().getTtfnid()).isEqualTo(versionList.getFirst().getTtfnid());
    assertThat(searchResult.get(1)).usingRecursiveComparison().isEqualTo(versionList.get(2));
    assertThat(searchResult.get(2)).usingRecursiveComparison().isEqualTo(versionList.get(3));
  }

  @Test
  void searchWithPageNumber1AndPageSize2() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(PageRequest.of(1, 2, Sort.by("descriptionOutwardLine1")))
            .searchCriterias(List.of("version"))
            .build()).toList();
    // Then
    assertThat(searchResult).hasSize(2);
    assertThat(searchResult.getFirst()).usingRecursiveComparison().isEqualTo(versionList.get(3));
    assertThat(searchResult.get(1)).usingRecursiveComparison().isEqualTo(versionList.get(4));
  }

  @Test
  void searchWithSortingDescriptionDesc() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(PageRequest.of(0, 10, Sort.by(Direction.DESC, "descriptionOutwardLine1")))
            .statusRestriction(Status.IN_REVIEW)
            .build()).toList();
    // Then
    assertThat(searchResult).hasSize(2);
    assertThat(searchResult.getFirst()).usingRecursiveComparison().isEqualTo(versionList.get(4));
    assertThat(searchResult.get(1).getTtfnid()).isEqualTo(versionList.getFirst().getTtfnid());
  }

  @Test
  void searchWithSortingTtfnidAndValidFromAsc() {
    // Given initial dataset
    // When
    List<TimetableFieldNumber> searchResult = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(PageRequest.of(0, 10, Sort.by(Direction.ASC, "ttfnid", "validFrom")))
            .build()).toList();
    // Then
    assertThat(searchResult).hasSize(4);
    assertThat(searchResult.getFirst().getTtfnid()).isEqualTo(versionList.getFirst().getTtfnid());
    assertThat(searchResult.get(1)).usingRecursiveComparison().isEqualTo(versionList.get(2));
    assertThat(searchResult.get(2)).usingRecursiveComparison().isEqualTo(versionList.get(3));
    assertThat(searchResult.get(3)).usingRecursiveComparison().isEqualTo(versionList.get(4));
  }

  @Test
  void searchVersionsWithValidAtAndTtfnids() {
    // Given initial dataset
    // When
    List<TimetableFieldNumberVersion> result =
        timetableFieldNumberService.getVersionsValidAt(Set.of(versionList.getFirst().getTtfnid()), SEARCH_DATE);
    // Then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getTtfnid()).isEqualTo(versionList.getFirst().getTtfnid());
  }
}