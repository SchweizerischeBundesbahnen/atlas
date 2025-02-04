package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.lidi.AffectedSublines;
import ch.sbb.atlas.api.lidi.SublineShorteningRequest;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SublineShorteningServiceTest {

  @Mock
  private LineVersionRepository lineVersionRepository;

  @Mock
  private SublineVersionRepository sublineVersionRepository;

  @Mock
  private SublineService sublineService;

  private SublineShorteningService sublineShorteningService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    sublineShorteningService = new SublineShorteningService(sublineService, sublineVersionRepository, lineVersionRepository);
  }

  @Test
  void shouldReturnAllowedSublinesOnly() {
    LineVersion lineVersion = LineVersion.builder()
        .id(1000L)
        .slnid("mainline")
        .validFrom(LocalDate.of(1999, 1, 1))
        .validTo(LocalDate.of(2017, 12, 31))
        .description("desc")
        .build();

    SublineVersion sublineVersion = SublineVersion.builder()
        .slnid("12345")
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2008, 12, 31))
        .description("oldestVersion")
        .build();

    SublineVersion sublineVersion2 = SublineVersion.builder()
        .slnid("12345")
        .validFrom(LocalDate.of(2009, 1, 1))
        .validTo(LocalDate.of(2016, 12, 31))
        .description("latestVersion")
        .build();

    SublineVersion sublineVersionNew = SublineVersion.builder()
        .slnid("4321")
        .validFrom(LocalDate.of(2002, 1, 1))
        .validTo(LocalDate.of(2008, 12, 31))
        .description("oldestVersion")
        .build();

    SublineVersion sublineVersionNew2 = SublineVersion.builder()
        .slnid("4321")
        .validFrom(LocalDate.of(2009, 1, 1))
        .validTo(LocalDate.of(2019, 12, 31))
        .description("latestVersion")
        .build();

    when(lineVersionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(lineVersion));
    when(sublineVersionRepository.getSublineVersionByMainlineSlnid("mainline")).thenReturn(
        List.of(sublineVersion, sublineVersion2, sublineVersionNew, sublineVersionNew2));

    AffectedSublines affectedSublines = sublineShorteningService.checkAffectedSublines(lineVersion.getId(),
        LocalDate.of(2000, 1, 1),
        LocalDate.of(2015, 12, 31));
    assertThat(affectedSublines.getAllowedSublines()).containsExactlyInAnyOrderElementsOf(
        List.of(sublineVersionNew2.getSlnid(), sublineVersion2.getSlnid()));
  }

  @Test
  void shouldReturnAllowedSublinesAndNotAllowedSublines() {
    LineVersion lineVersion = LineVersion.builder()
        .id(1000L)
        .slnid("mainline")
        .validFrom(LocalDate.of(2004, 1, 1))
        .validTo(LocalDate.of(2015, 12, 31))
        .description("desc")
        .build();

    SublineVersion allowedSublineVersion = SublineVersion.builder()
        .slnid("12345")
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2008, 12, 31))
        .description("oldestVersion")
        .build();

    SublineVersion allowedSublineVersion2 = SublineVersion.builder()
        .slnid("12345")
        .validFrom(LocalDate.of(2009, 1, 1))
        .validTo(LocalDate.of(2016, 12, 31))
        .description("latestVersion")
        .build();

    SublineVersion notAllowedSublineVersion = SublineVersion.builder()
        .slnid("4321")
        .validFrom(LocalDate.of(2002, 1, 1))
        .validTo(LocalDate.of(2003, 12, 31))
        .description("oldestVersion")
        .build();

    SublineVersion notAllowedSublineVersion2 = SublineVersion.builder()
        .slnid("4321")
        .validFrom(LocalDate.of(2016, 1, 1))
        .validTo(LocalDate.of(2019, 12, 31))
        .description("latestVersion")
        .build();

    when(lineVersionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(lineVersion));
    when(sublineVersionRepository.getSublineVersionByMainlineSlnid("mainline")).thenReturn(
        List.of(allowedSublineVersion, allowedSublineVersion2, notAllowedSublineVersion, notAllowedSublineVersion2));

    AffectedSublines affectedSublines = sublineShorteningService.checkAffectedSublines(lineVersion.getId(),
        lineVersion.getValidFrom(),
        lineVersion.getValidTo());
    assertThat(affectedSublines.getAllowedSublines()).containsExactlyInAnyOrderElementsOf(
        List.of(allowedSublineVersion.getSlnid()));
    assertThat(affectedSublines.getNotAllowedSublines()).containsExactlyInAnyOrderElementsOf(
        List.of(notAllowedSublineVersion.getSlnid()));
  }

  @Test
  void shouldReturnNotAllowedSublines() {
    LineVersion lineVersion = LineVersion.builder()
        .id(1000L)
        .slnid("mainline")
        .validFrom(LocalDate.of(1999, 1, 1))
        .validTo(LocalDate.of(2017, 12, 31))
        .description("desc")
        .build();

    SublineVersion notAllowedSublineVersion = SublineVersion.builder()
        .slnid("12345")
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2003, 12, 31))
        .description("oldestVersion")
        .build();

    SublineVersion notAllowedSublineVersion2 = SublineVersion.builder()
        .slnid("12345")
        .validFrom(LocalDate.of(2013, 1, 1))
        .validTo(LocalDate.of(2016, 12, 31))
        .description("latestVersion")
        .build();

    SublineVersion notAllowedSublineVersion3 = SublineVersion.builder()
        .slnid("4321")
        .validFrom(LocalDate.of(2002, 1, 1))
        .validTo(LocalDate.of(2003, 12, 31))
        .description("oldestVersion")
        .build();

    SublineVersion notAllowedSublineVersion4 = SublineVersion.builder()
        .slnid("4321")
        .validFrom(LocalDate.of(2016, 1, 1))
        .validTo(LocalDate.of(2019, 12, 31))
        .description("latestVersion")
        .build();

    when(lineVersionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(lineVersion));
    when(sublineVersionRepository.getSublineVersionByMainlineSlnid("mainline")).thenReturn(
        List.of(notAllowedSublineVersion, notAllowedSublineVersion2, notAllowedSublineVersion3, notAllowedSublineVersion4));

    AffectedSublines affectedSublines = sublineShorteningService.checkAffectedSublines(lineVersion.getId(),
        LocalDate.of(2004, 1, 1),
        LocalDate.of(2015, 12, 31));
    assertThat(affectedSublines.getNotAllowedSublines()).containsExactlyInAnyOrderElementsOf(
        List.of(notAllowedSublineVersion.getSlnid(),
            notAllowedSublineVersion4.getSlnid()));
  }

  @Test
  void shouldReturnNotAllowedSublinesIfCompletlyOutOfRange() {
    LineVersion lineVersion = LineVersion.builder()
        .id(1000L)
        .slnid("mainline")
        .validFrom(LocalDate.of(2004, 1, 1))
        .validTo(LocalDate.of(2015, 12, 31))
        .description("desc")
        .build();

    SublineVersion notAllowedSublineVersion3 = SublineVersion.builder()
        .slnid("4321")
        .validFrom(LocalDate.of(2025, 1, 1))
        .validTo(LocalDate.of(2025, 12, 31))
        .description("oldestVersion")
        .build();

    when(lineVersionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(lineVersion));
    when(sublineVersionRepository.getSublineVersionByMainlineSlnid("mainline")).thenReturn(
        List.of(notAllowedSublineVersion3));

    AffectedSublines affectedSublines = sublineShorteningService.checkAffectedSublines(lineVersion.getId(),
        LocalDate.of(2000, 1, 1),
        LocalDate.of(2014, 12, 31));
    assertThat(affectedSublines.getNotAllowedSublines()).containsExactlyInAnyOrderElementsOf(
        List.of(notAllowedSublineVersion3.getSlnid()));
  }

  @Test
  void shouldShortSublines() {
    LineVersion lineVersion = LineTestData.lineVersion();
    lineVersion.setId(1000L);
    lineVersion.setSlnid("mainline");
    lineVersion.setValidFrom(LocalDate.of(1999, 1, 1));
    lineVersion.setValidTo(LocalDate.of(2007, 1, 1));

    LineVersion editedVersion = LineTestData.lineVersion();
    editedVersion.setSlnid("mainline");
    editedVersion.setValidFrom(LocalDate.of(1999, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2006, 1, 1));

    DateRange mainlineDateRange = new DateRange(
        editedVersion.getValidFrom(),
        editedVersion.getValidTo()
    );

    SublineVersion sublineVersion = SublineVersion.builder()
        .slnid("1234")
        .validFrom(LocalDate.of(1999, 1, 1))
        .validTo(LocalDate.of(2008, 1, 1))
        .description("version 1")
        .build();

    SublineVersion editedSubline = SublineVersion.builder()
        .slnid("1234")
        .validFrom(LocalDate.of(1999, 1, 1))
        .validTo(LocalDate.of(2006, 1, 1))
        .description("version 1")
        .build();

    List<String> slnid = new ArrayList<>();
    slnid.add(sublineVersion.getSlnid());
    SublineShorteningRequest sublineShorteningRequest = new SublineShorteningRequest(mainlineDateRange, slnid);

    when(sublineShorteningService.findById(anyLong())).thenReturn(Optional.of(lineVersion));
    when(sublineVersionRepository.findAllBySlnidOrderByValidFrom(anyString())).thenReturn(
        List.of(sublineVersion));

    sublineShorteningService.shortSublines(lineVersion.getId(), sublineShorteningRequest);
    verify(sublineService, times(1)).updateVersion(refEq(sublineVersion), refEq(editedSubline));

  }

  @Test
  void testCheckAndShortSublines_callsShortSublines_whenConditionsMet() {
    SublineShorteningService serviceSpy = spy(
        new SublineShorteningService(sublineService, sublineVersionRepository, lineVersionRepository)
    );

    LineVersion lineVersion = LineVersion.builder()
        .id(1000L)
        .slnid("mainline")
        .validFrom(LocalDate.of(2004, 1, 1))
        .validTo(LocalDate.of(2015, 12, 31))
        .description("desc")
        .build();

    LineVersion editedVersion = new LineVersion();
    editedVersion.setSlnid("mainline");
    editedVersion.setValidFrom(LocalDate.of(2006, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2014, 11, 30));

    List<String> allowedSublines = List.of("1234");
    List<String> notAllowedSublines = Collections.emptyList();

    SublineVersion sublineVersion = SublineVersion.builder()
        .slnid("1234")
        .validFrom(LocalDate.of(1999, 1, 1))
        .validTo(LocalDate.of(2015, 1, 1))
        .description("version 1")
        .build();

    AffectedSublines affectedSublines = new AffectedSublines(allowedSublines, notAllowedSublines);

    doReturn(Optional.of(lineVersion))
        .when(serviceSpy)
        .findById(anyLong());

    when(sublineVersionRepository.getSublineVersionByMainlineSlnid(anyString()))
        .thenReturn(List.of(sublineVersion));

    when(sublineVersionRepository.findAllBySlnidOrderByValidFrom(anyString()))
        .thenReturn(List.of(sublineVersion));

    doReturn(affectedSublines)
        .when(serviceSpy)
        .checkAffectedSublines(anyLong(), eq(editedVersion.getValidFrom()), eq(editedVersion.getValidTo()));

    serviceSpy.checkAndShortSublines(lineVersion, editedVersion);

    DateRange expectedDateRange = new DateRange(editedVersion.getValidFrom(), editedVersion.getValidTo());
    SublineShorteningRequest expectedRequest = new SublineShorteningRequest(expectedDateRange, allowedSublines);

    verify(serviceSpy, times(1))
        .shortSublines(eq(lineVersion.getId()), refEq(expectedRequest));
  }
}