package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.lidi.AffectedSublinesModel;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.model.SublineVersionRange;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SublineShorteningServiceTest {

  @Mock
  private LineVersionRepository lineVersionRepository;

  @Mock
  private SublineVersionRepository sublineVersionRepository;

  private SublineShorteningService sublineShorteningService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    sublineShorteningService = new SublineShorteningService(sublineVersionRepository, lineVersionRepository);
  }

  @Test
  void shouldReturnAllowedSublinesOnlyOneVersion() {
    LineVersion lineVersion = LineVersion.builder()
        .id(1000L)
        .slnid("mainline")
        .validFrom(LocalDate.of(1999, 1, 1))
        .validTo(LocalDate.of(2017, 12, 31))
        .description("desc")
        .build();

    LineVersion editedVersion = LineVersion.builder()
        .id(1000L)
        .slnid("mainline")
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2015, 12, 31))
        .description("desc")
        .build();

    SublineVersion sublineVersion = SublineVersion.builder()
        .slnid("12345")
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2008, 12, 31))
        .description("oldestVersion")
        .build();

    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(anyString())).thenReturn(new ArrayList<>(List.of(lineVersion)));
    when(sublineVersionRepository.getSublineVersionByMainlineSlnid(anyString())).thenReturn(
        new ArrayList<>(List.of(sublineVersion)));

    AffectedSublinesModel affectedSublinesModel = sublineShorteningService.checkAffectedSublines(lineVersion, editedVersion);
    assertThat(affectedSublinesModel.getAllowedSublines()).containsExactlyInAnyOrderElementsOf(
        List.of(sublineVersion.getSlnid()));
  }

  @Test
  void shouldReturnAllowedSublinesOnlyMultipleSublineVersions() {
    LineVersion lineVersion = LineVersion.builder()
        .id(1000L)
        .slnid("mainline")
        .validFrom(LocalDate.of(1999, 1, 1))
        .validTo(LocalDate.of(2017, 12, 31))
        .description("desc")
        .build();

    LineVersion editedVersion = LineVersion.builder()
        .id(1000L)
        .slnid("mainline")
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2015, 12, 31))
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

    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(anyString())).thenReturn(new ArrayList<>(List.of(lineVersion)));
    when(sublineVersionRepository.getSublineVersionByMainlineSlnid(anyString())).thenReturn(
        new ArrayList<>(List.of(sublineVersion, sublineVersion2, sublineVersionNew, sublineVersionNew2)));

    AffectedSublinesModel affectedSublinesModel = sublineShorteningService.checkAffectedSublines(lineVersion, editedVersion);
    assertThat(affectedSublinesModel.getAllowedSublines()).containsExactlyInAnyOrderElementsOf(
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

    LineVersion editedVersion = LineVersion.builder()
        .id(1000L)
        .slnid("mainline")
        .validFrom(LocalDate.of(2004, 1, 31))
        .validTo(LocalDate.of(2016, 1, 1))
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

    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(anyString())).thenReturn(new ArrayList<>(List.of(lineVersion)));
    when(sublineVersionRepository.getSublineVersionByMainlineSlnid(anyString())).thenReturn(
        List.of(allowedSublineVersion, allowedSublineVersion2, notAllowedSublineVersion, notAllowedSublineVersion2));

    AffectedSublinesModel affectedSublinesModel = sublineShorteningService.checkAffectedSublines(lineVersion, editedVersion);
    assertThat(affectedSublinesModel.getAllowedSublines()).containsExactlyInAnyOrderElementsOf(
        List.of(allowedSublineVersion.getSlnid()));
    assertThat(affectedSublinesModel.getNotAllowedSublines()).containsExactlyInAnyOrderElementsOf(
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

    LineVersion editedVersion = LineVersion.builder()
        .id(1000L)
        .slnid("mainline")
        .validFrom(LocalDate.of(2004, 1, 1))
        .validTo(LocalDate.of(2015, 12, 31))
        .description("desc")
        .build();

    SublineVersion notAllowedSublineVersion = SublineVersion.builder()
        .slnid("12345")
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .description("oldestVersion")
        .build();

    SublineVersion notAllowedSublineVersion2 = SublineVersion.builder()
        .slnid("12345")
        .validFrom(LocalDate.of(2003, 1, 1))
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

    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(anyString())).thenReturn(new ArrayList<>(List.of(lineVersion)));
    when(sublineVersionRepository.getSublineVersionByMainlineSlnid(anyString())).thenReturn(
        new ArrayList<>(List.of(notAllowedSublineVersion, notAllowedSublineVersion2, notAllowedSublineVersion3,
            notAllowedSublineVersion4)));

    AffectedSublinesModel affectedSublinesModel = sublineShorteningService.checkAffectedSublines(lineVersion, editedVersion);
    assertThat(affectedSublinesModel.getNotAllowedSublines()).containsExactlyInAnyOrderElementsOf(
        List.of(notAllowedSublineVersion.getSlnid(),
            notAllowedSublineVersion4.getSlnid()));
  }

  @Test
  void checkAndPrepareToShortSublines() {
    LineVersion lineVersion = LineTestData.lineVersion();
    lineVersion.setId(1000L);
    lineVersion.setSlnid("mainline");
    lineVersion.setValidFrom(LocalDate.of(1999, 1, 1));
    lineVersion.setValidTo(LocalDate.of(2017, 1, 1));

    LineVersion editedVersion = LineTestData.lineVersion();
    editedVersion.setSlnid("mainline");
    editedVersion.setValidFrom(LocalDate.of(1999, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2015, 1, 1));

    SublineVersion sublineVersion = SublineVersion.builder()
        .slnid("1234")
        .validFrom(LocalDate.of(1999, 1, 1))
        .validTo(LocalDate.of(2008, 1, 1))
        .description("version 1")
        .mainlineSlnid("mainline")
        .build();

    SublineVersion sublineVersion2 = SublineVersion.builder()
        .slnid("1234")
        .validFrom(LocalDate.of(2008, 1, 2))
        .validTo(LocalDate.of(2016, 1, 1))
        .description("version 2")
        .mainlineSlnid("mainline")
        .build();

    List<String> slnid = new ArrayList<>();
    slnid.add(sublineVersion.getSlnid());

    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(anyString())).thenReturn(new ArrayList<>(List.of(lineVersion)));

    when(sublineVersionRepository.getSublineVersionByMainlineSlnid(anyString())).thenReturn(
        new ArrayList<>(List.of(sublineVersion, sublineVersion2)));

    when(sublineVersionRepository.findAllBySlnidOrderByValidFrom("1234")).thenReturn(
        new ArrayList<>(List.of(sublineVersion, sublineVersion2)));

    List<SublineVersionRange> sublinesToShort = sublineShorteningService.checkAndPrepareToShortSublines(lineVersion,
        editedVersion);

    assertThat(sublinesToShort).hasSize(1);
  }

  @Test
  void shouldReturnAllowedSublinesWhenMultipleLineVersionsValidTo() {
    LineVersion lineVersion1 = LineVersion.builder()
        .id(1000L)
        .slnid("mainline")
        .validFrom(LocalDate.of(1999, 1, 1))
        .validTo(LocalDate.of(2010, 12, 31))
        .description("desc")
        .build();

    LineVersion lineVersion2 = LineVersion.builder()
        .id(1001L)
        .slnid("mainline")
        .validFrom(LocalDate.of(2011, 1, 1))
        .validTo(LocalDate.of(2017, 12, 31))
        .description("desc2")
        .build();

    LineVersion editedVersion = LineVersion.builder()
        .id(1001L)
        .slnid("mainline")
        .validFrom(LocalDate.of(2011, 1, 1))
        .validTo(LocalDate.of(2016, 1, 1))
        .description("desc2")
        .build();

    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(anyString())).thenReturn(new ArrayList<>(List.of(lineVersion1,
        lineVersion2)));

    SublineVersion sublineVersion1 = SublineVersion.builder()
        .slnid("12345")
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .description("oldestVersion")
        .build();

    SublineVersion sublineVersion2 = SublineVersion.builder()
        .slnid("12345")
        .validFrom(LocalDate.of(2003, 1, 1))
        .validTo(LocalDate.of(2016, 12, 31))
        .description("latestVersion")
        .build();

    SublineVersion sublineVersion3 = SublineVersion.builder()
        .slnid("4321")
        .validFrom(LocalDate.of(2002, 1, 1))
        .validTo(LocalDate.of(2003, 12, 31))
        .description("oldestVersion")
        .build();

    SublineVersion sublineVersion4 = SublineVersion.builder()
        .slnid("4321")
        .validFrom(LocalDate.of(2004, 1, 1))
        .validTo(LocalDate.of(2019, 12, 31))
        .description("latestVersion")
        .build();

    when(sublineVersionRepository.getSublineVersionByMainlineSlnid(anyString())).thenReturn(
        new ArrayList<>(List.of(sublineVersion1, sublineVersion2, sublineVersion3, sublineVersion4)));

    AffectedSublinesModel affectedSublinesModel = sublineShorteningService.checkAffectedSublines(lineVersion2, editedVersion);

    assertThat(affectedSublinesModel.getAllowedSublines()).containsExactlyInAnyOrderElementsOf(
        List.of(sublineVersion1.getSlnid(),
            sublineVersion3.getSlnid()));
  }

  @Test
  void shouldReturnAllowedSublinesWhenMultipleLineVersionsValidFrom() {
    LineVersion lineVersion1 = LineVersion.builder()
        .id(1000L)
        .slnid("mainline")
        .validFrom(LocalDate.of(1999, 1, 1))
        .validTo(LocalDate.of(2010, 12, 31))
        .description("desc")
        .build();

    LineVersion editedVersion = LineVersion.builder()
        .id(1000L)
        .slnid("mainline")
        .validFrom(LocalDate.of(2003, 12, 31))
        .validTo(LocalDate.of(2010, 12, 31))
        .description("desc")
        .build();

    LineVersion lineVersion2 = LineVersion.builder()
        .id(1001L)
        .slnid("mainline")
        .validFrom(LocalDate.of(2011, 1, 1))
        .validTo(LocalDate.of(2017, 12, 31))
        .description("desc2")
        .build();

    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(anyString())).thenReturn(new ArrayList<>(List.of(lineVersion1,
        lineVersion2)));

    SublineVersion sublineVersion1 = SublineVersion.builder()
        .slnid("12345")
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2004, 12, 31))
        .description("oldestVersion")
        .build();

    SublineVersion sublineVersion2 = SublineVersion.builder()
        .slnid("12345")
        .validFrom(LocalDate.of(2005, 1, 1))
        .validTo(LocalDate.of(2016, 12, 31))
        .description("latestVersion")
        .build();

    SublineVersion sublineVersion3 = SublineVersion.builder()
        .slnid("4321")
        .validFrom(LocalDate.of(2002, 1, 1))
        .validTo(LocalDate.of(2006, 12, 31))
        .description("oldestVersion")
        .build();

    SublineVersion sublineVersion4 = SublineVersion.builder()
        .slnid("4321")
        .validFrom(LocalDate.of(2007, 1, 1))
        .validTo(LocalDate.of(2019, 12, 31))
        .description("latestVersion")
        .build();

    when(sublineVersionRepository.getSublineVersionByMainlineSlnid(anyString())).thenReturn(
        new ArrayList<>(List.of(sublineVersion1, sublineVersion2, sublineVersion3, sublineVersion4)));

    AffectedSublinesModel affectedSublinesModel = sublineShorteningService.checkAffectedSublines(lineVersion1, editedVersion);

    assertThat(affectedSublinesModel.getAllowedSublines()).containsExactlyInAnyOrderElementsOf(
        List.of(sublineVersion1.getSlnid(),
            sublineVersion3.getSlnid()));
  }

  @Test
  void shouldReturnEmptyWhenValidityIsNotAffected() {
    LineVersion lineVersion1 = LineVersion.builder()
        .id(1000L)
        .slnid("mainline")
        .validFrom(LocalDate.of(1999, 1, 1))
        .validTo(LocalDate.of(2010, 12, 31))
        .description("desc")
        .build();

    LineVersion lineVersion2 = LineVersion.builder()
        .id(1001L)
        .slnid("mainline")
        .validFrom(LocalDate.of(2011, 1, 1))
        .validTo(LocalDate.of(2017, 12, 31))
        .description("desc2")
        .build();

    LineVersion editedVersion = LineVersion.builder()
        .id(1001L)
        .slnid("mainline")
        .validFrom(LocalDate.of(2011, 1, 1))
        .validTo(LocalDate.of(2016, 12, 31))
        .description("desc2")
        .build();

    LineVersion lineVersion3 = LineVersion.builder()
        .id(1002L)
        .slnid("mainline")
        .validFrom(LocalDate.of(2017, 1, 1))
        .validTo(LocalDate.of(2027, 12, 31))
        .description("desc2")
        .build();

    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(anyString())).thenReturn(new ArrayList<>(List.of(lineVersion1,
        lineVersion2, lineVersion3)));

    SublineVersion sublineVersion1 = SublineVersion.builder()
        .slnid("12345")
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2004, 12, 31))
        .description("oldestVersion")
        .build();

    SublineVersion sublineVersion2 = SublineVersion.builder()
        .slnid("12345")
        .validFrom(LocalDate.of(2005, 1, 1))
        .validTo(LocalDate.of(2016, 12, 31))
        .description("latestVersion")
        .build();

    SublineVersion sublineVersion3 = SublineVersion.builder()
        .slnid("4321")
        .validFrom(LocalDate.of(2002, 1, 1))
        .validTo(LocalDate.of(2006, 12, 31))
        .description("oldestVersion")
        .build();

    SublineVersion sublineVersion4 = SublineVersion.builder()
        .slnid("4321")
        .validFrom(LocalDate.of(2007, 1, 1))
        .validTo(LocalDate.of(2019, 12, 31))
        .description("latestVersion")
        .build();

    when(sublineVersionRepository.getSublineVersionByMainlineSlnid(anyString())).thenReturn(
        new ArrayList<>(List.of(sublineVersion1, sublineVersion2, sublineVersion3, sublineVersion4)));

    AffectedSublinesModel affectedSublinesModel = sublineShorteningService.checkAffectedSublines(lineVersion2, editedVersion);

    assertThat(affectedSublinesModel.getAllowedSublines()).isEmpty();
    assertThat(affectedSublinesModel.getNotAllowedSublines()).isEmpty();
  }

  @Test
  void shouldReturnEmptyWhenOldestVersionValidToChanges() {
    LineVersion lineVersion1 = LineVersion.builder()
        .id(1000L)
        .slnid("mainline")
        .validFrom(LocalDate.of(1999, 1, 1))
        .validTo(LocalDate.of(2010, 12, 31))
        .description("desc")
        .build();

    LineVersion editedVersion = LineVersion.builder()
        .id(1000L)
        .slnid("mainline")
        .validFrom(LocalDate.of(1999, 1, 1))
        .validTo(LocalDate.of(2009, 12, 31))
        .description("desc")
        .build();

    LineVersion lineVersion2 = LineVersion.builder()
        .id(1001L)
        .slnid("mainline")
        .validFrom(LocalDate.of(2011, 1, 1))
        .validTo(LocalDate.of(2017, 12, 31))
        .description("desc2")
        .build();

    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(anyString())).thenReturn(new ArrayList<>(List.of(lineVersion1,
        lineVersion2)));

    SublineVersion sublineVersion1 = SublineVersion.builder()
        .slnid("12345")
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2004, 12, 31))
        .description("oldestVersion")
        .build();

    SublineVersion sublineVersion2 = SublineVersion.builder()
        .slnid("12345")
        .validFrom(LocalDate.of(2005, 1, 1))
        .validTo(LocalDate.of(2016, 12, 31))
        .description("latestVersion")
        .build();

    SublineVersion sublineVersion3 = SublineVersion.builder()
        .slnid("4321")
        .validFrom(LocalDate.of(2002, 1, 1))
        .validTo(LocalDate.of(2006, 12, 31))
        .description("oldestVersion")
        .build();

    SublineVersion sublineVersion4 = SublineVersion.builder()
        .slnid("4321")
        .validFrom(LocalDate.of(2007, 1, 1))
        .validTo(LocalDate.of(2019, 12, 31))
        .description("latestVersion")
        .build();

    when(sublineVersionRepository.getSublineVersionByMainlineSlnid(anyString())).thenReturn(
        new ArrayList<>(List.of(sublineVersion1, sublineVersion2, sublineVersion3, sublineVersion4)));

    AffectedSublinesModel affectedSublinesModel = sublineShorteningService.checkAffectedSublines(lineVersion1, editedVersion);

    assertThat(affectedSublinesModel.getAllowedSublines()).isEmpty();
    assertThat(affectedSublinesModel.getNotAllowedSublines()).isEmpty();
  }

  @Test
  void shouldReturnEmptyWhenLatestVersionValidFromChanges() {
    LineVersion lineVersion1 = LineVersion.builder()
        .id(1000L)
        .slnid("mainline")
        .validFrom(LocalDate.of(1999, 1, 1))
        .validTo(LocalDate.of(2010, 12, 31))
        .description("desc")
        .build();

    LineVersion lineVersion2 = LineVersion.builder()
        .id(1001L)
        .slnid("mainline")
        .validFrom(LocalDate.of(2011, 1, 1))
        .validTo(LocalDate.of(2017, 12, 31))
        .description("desc2")
        .build();

    LineVersion editedVersion = LineVersion.builder()
        .id(1001L)
        .slnid("mainline")
        .validFrom(LocalDate.of(2013, 1, 1))
        .validTo(LocalDate.of(2017, 12, 31))
        .description("desc2")
        .build();

    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(anyString())).thenReturn(new ArrayList<>(List.of(lineVersion1,
        lineVersion2)));

    SublineVersion sublineVersion1 = SublineVersion.builder()
        .slnid("12345")
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2004, 12, 31))
        .description("oldestVersion")
        .build();

    SublineVersion sublineVersion2 = SublineVersion.builder()
        .slnid("12345")
        .validFrom(LocalDate.of(2005, 1, 1))
        .validTo(LocalDate.of(2016, 12, 31))
        .description("latestVersion")
        .build();

    SublineVersion sublineVersion3 = SublineVersion.builder()
        .slnid("4321")
        .validFrom(LocalDate.of(2002, 1, 1))
        .validTo(LocalDate.of(2006, 12, 31))
        .description("oldestVersion")
        .build();

    SublineVersion sublineVersion4 = SublineVersion.builder()
        .slnid("4321")
        .validFrom(LocalDate.of(2007, 1, 1))
        .validTo(LocalDate.of(2019, 12, 31))
        .description("latestVersion")
        .build();

    when(sublineVersionRepository.getSublineVersionByMainlineSlnid(anyString())).thenReturn(
        new ArrayList<>(List.of(sublineVersion1, sublineVersion2, sublineVersion3, sublineVersion4)));

    AffectedSublinesModel affectedSublinesModel = sublineShorteningService.checkAffectedSublines(lineVersion2, editedVersion);

    assertThat(affectedSublinesModel.getAllowedSublines()).isEmpty();
    assertThat(affectedSublinesModel.getNotAllowedSublines()).isEmpty();
  }

  @Test
  void shouldReturnAllSublinesToShort() {
    LineVersion lineVersion = LineVersion.builder()
        .id(1000L)
        .slnid("mainline")
        .validFrom(LocalDate.of(1999, 1, 1))
        .validTo(LocalDate.of(2015, 12, 31))
        .description("desc")
        .build();

    LineVersion editedVersion = LineVersion.builder()
        .id(1000L)
        .slnid("mainline")
        .validFrom(LocalDate.of(1999, 1, 1))
        .validTo(LocalDate.of(2010, 12, 31))
        .description("desc")
        .build();

    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(anyString())).thenReturn(new ArrayList<>(List.of(lineVersion)));

    SublineVersion sublineVersion1 = SublineVersion.builder()
        .slnid("12345")
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2004, 12, 31))
        .description("oldestVersion")
        .build();

    SublineVersion sublineVersion2 = SublineVersion.builder()
        .slnid("12345")
        .validFrom(LocalDate.of(2005, 1, 1))
        .validTo(LocalDate.of(2016, 12, 31))
        .description("latestVersion")
        .build();

    SublineVersion sublineVersion3 = SublineVersion.builder()
        .slnid("4321")
        .validFrom(LocalDate.of(2002, 1, 1))
        .validTo(LocalDate.of(2006, 12, 31))
        .description("oldestVersion")
        .build();

    SublineVersion sublineVersion4 = SublineVersion.builder()
        .slnid("4321")
        .validFrom(LocalDate.of(2007, 1, 1))
        .validTo(LocalDate.of(2019, 12, 31))
        .description("latestVersion")
        .build();

    when(sublineVersionRepository.getSublineVersionByMainlineSlnid(anyString())).thenReturn(
        new ArrayList<>(List.of(sublineVersion1, sublineVersion2, sublineVersion3, sublineVersion4)));

    when(sublineVersionRepository.findAllBySlnidOrderByValidFrom("12345")).thenReturn(
        new ArrayList<>(List.of(sublineVersion1, sublineVersion2)));

    when(sublineVersionRepository.findAllBySlnidOrderByValidFrom("4321")).thenReturn(
        new ArrayList<>(List.of(sublineVersion3, sublineVersion4)));

    List<SublineVersionRange> list = sublineShorteningService.checkAndPrepareToShortSublines(lineVersion, editedVersion);
    assertThat(list).isNotEmpty();
    assertThat(list).hasSize(2);
  }

  @Test
  void shouldReturnOneSublineToShort() {
    LineVersion lineVersion = LineVersion.builder()
        .id(1000L)
        .slnid("mainline")
        .validFrom(LocalDate.of(1999, 1, 1))
        .validTo(LocalDate.of(2015, 12, 31))
        .description("desc")
        .build();

    LineVersion editedVersion = LineVersion.builder()
        .id(1000L)
        .slnid("mainline")
        .validFrom(LocalDate.of(1999, 1, 1))
        .validTo(LocalDate.of(2010, 12, 31))
        .description("desc")
        .build();

    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(anyString())).thenReturn(new ArrayList<>(List.of(lineVersion)));

    SublineVersion sublineVersion1 = SublineVersion.builder()
        .slnid("12345")
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2011, 12, 31))
        .description("oldestVersion")
        .build();

    SublineVersion sublineVersion2 = SublineVersion.builder()
        .slnid("12345")
        .validFrom(LocalDate.of(2012, 1, 1))
        .validTo(LocalDate.of(2016, 12, 31))
        .description("latestVersion")
        .build();

    SublineVersion sublineVersion3 = SublineVersion.builder()
        .slnid("4321")
        .validFrom(LocalDate.of(2002, 1, 1))
        .validTo(LocalDate.of(2006, 12, 31))
        .description("oldestVersion")
        .build();

    SublineVersion sublineVersion4 = SublineVersion.builder()
        .slnid("4321")
        .validFrom(LocalDate.of(2007, 1, 1))
        .validTo(LocalDate.of(2019, 12, 31))
        .description("latestVersion")
        .build();

    when(sublineVersionRepository.getSublineVersionByMainlineSlnid(anyString())).thenReturn(
        new ArrayList<>(List.of(sublineVersion1, sublineVersion2, sublineVersion3, sublineVersion4)));

    when(sublineVersionRepository.findAllBySlnidOrderByValidFrom("12345")).thenReturn(
        new ArrayList<>(List.of(sublineVersion1, sublineVersion2)));

    when(sublineVersionRepository.findAllBySlnidOrderByValidFrom("4321")).thenReturn(
        new ArrayList<>(List.of(sublineVersion3, sublineVersion4)));

    List<SublineVersionRange> list = sublineShorteningService.checkAndPrepareToShortSublines(lineVersion, editedVersion);
    assertThat(list).isNotEmpty();
    assertThat(list).hasSize(1);
  }
}