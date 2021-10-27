package ch.sbb.timetable.field.number.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.timetable.field.number.IntegrationTest;
import ch.sbb.timetable.field.number.entity.LineRelation;
import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.repository.VersionRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class VersionServiceTest {

  private static final String TTFNID = "ch:1:fpfnid:100000";
  private final VersionRepository versionRepository;
  private final VersionService versionService;

  private Version version1;
  private Version version2;
  private Version version3;

  @Autowired
  public VersionServiceTest(VersionRepository versionRepository,
      VersionService versionService) {
    this.versionRepository = versionRepository;
    this.versionService = versionService;
  }

  @BeforeEach
  void init() {
    version1 = Version.builder().ttfnid(TTFNID)
                      .name("FPFN Name")
                      .number("BEX")
                      .swissTimetableFieldNumber("b0.BEX")
                      .validFrom(LocalDate.of(2020, 1, 1))
                      .validTo(LocalDate.of(2021, 12, 31))
                      .build();
    version2 = Version.builder().ttfnid(TTFNID)
                      .name("FPFN Name")
                      .number("BEX")
                      .swissTimetableFieldNumber("b0.BEX")
                      .validFrom(LocalDate.of(2022, 1, 1))
                      .validTo(LocalDate.of(2023, 12, 31))
                      .build();
    version3 = Version.builder().ttfnid(TTFNID)
                      .name("FPFN Name")
                      .number("BEX")
                      .swissTimetableFieldNumber("b0.BEX")
                      .validFrom(LocalDate.of(2024, 1, 1))
                      .validTo(LocalDate.of(2024, 12, 31))
                      .build();
  }

  @AfterEach
  void cleanUp() {
    List<Version> versionsVersioned = versionRepository.getAllVersionsVersioned(TTFNID);
    versionRepository.deleteAll(versionsVersioned);
  }

  /**
   * Szenario 1a: Update einer bestehenden Version am Ende
   * NEU:                             |________________________________
   * IST:      |----------------------|--------------------------------
   * Version:        1                                2
   *
   * RESULTAT: |----------------------|________________________________
   * Version:        1                                2
   */
  @Test
  public void scenario1a() {
    //given
    version2.setLineRelations(new HashSet<>(
        Set.of(LineRelation.builder().slnid(TTFNID).version(version2).build(),
            LineRelation.builder().slnid(TTFNID).version(version2).build())));
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);

    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <CHANGED>");
    editedVersion.getLineRelations().add(LineRelation.builder().slnid("ch:1:fpfnid:111111").version(version2).build());
    //when
    versionService.updateVersion(version2, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version2.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(2);
    result.sort(Comparator.comparing(Version::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name");

    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name <CHANGED>");
    assertThat(secondTemporalVersion.getLineRelations()).isNotEmpty();
    assertThat(secondTemporalVersion.getLineRelations().size()).isEqualTo(1);
    Set<LineRelation> lineRelations = secondTemporalVersion.getLineRelations();
    LineRelation lineRelation = lineRelations.stream().iterator().next();
    assertThat(lineRelation).isNotNull();
    assertThat(lineRelation.getSlnid()).isEqualTo("ch:1:fpfnid:111111");
  }

  /**
   * Szenario 1b: Update einer bestehenden Version in der Mitte
   * NEU:                  |______________________|
   * IST:      |-----------|----------------------|--------------------
   * Version:        1                 2                  3
   *
   * RESULTAT: |-----------|______________________|--------------------
   * Version:        1                 2                  3
   */
  @Test
  public void scenario1b() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);

    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");

    //when
    versionService.updateVersion(version2, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version2.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(3);
    result.sort(Comparator.comparing(Version::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name");

    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");

    Version thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(thirdTemporalVersion.getName()).isEqualTo("FPFN Name");
  }

  /**
   * Szenario 1c: Update einer bestehenden Version am Anfang
   *
   * NEU:       |___________|
   * IST:       |-----------|----------------------|--------------------
   * Version:         1                 2                   3
   *
   * RESULTAT: |___________|----------------------|--------------------
   * Version:        1                 2                  3
   */
  @Test
  public void scenario1c() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);

    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");
    editedVersion.setComment("A comment");

    //when
    versionService.updateVersion(version1, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(3);
    result.sort(Comparator.comparing(Version::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");
    assertThat(firstTemporalVersion.getComment()).isEqualTo("A comment");

    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(secondTemporalVersion.getComment()).isNull();

    Version thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(thirdTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(thirdTemporalVersion.getComment()).isNull();
  }

  /** Which scenario 2
   * Szenario 2: Update innerhalb existierender Version
   * NEU:                       |___________|
   * IST:      |-----------|----------------------|--------------------
   * Version:        1                 2                  3
   *
   * RESULTAT: |-----------|----|___________|-----|--------------------     NEUE VERSION EINGEFÜGT
   * Version:        1       2         4       5          3
   */
  @Test
  public void scenario2() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);
    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");
    editedVersion.setComment("Scenario 2");
    editedVersion.setValidFrom(LocalDate.of(2022, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2023, 6, 1));
    editedVersion.getLineRelations().add(LineRelation.builder().slnid("ch:1:fpfnid:111111").version(version2).build());


    //when
    versionService.updateVersion(version2, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(5);
    result.sort(Comparator.comparing(Version::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(firstTemporalVersion.getComment()).isNull();

    //updated
    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2022, 5, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(secondTemporalVersion.getComment()).isNull();

    //new
    Version thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 6, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 6, 1));
    assertThat(thirdTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");
    assertThat(thirdTemporalVersion.getComment()).isEqualTo("Scenario 2");
    Set<LineRelation> lineRelations = thirdTemporalVersion.getLineRelations();
    assertThat(lineRelations).isNotEmpty();
    assertThat(lineRelations.size()).isEqualTo(1);
    LineRelation lineRelation = lineRelations.stream().iterator().next();
    assertThat(lineRelation).isNotNull();
    assertThat(lineRelation.getSlnid()).isEqualTo("ch:1:fpfnid:111111");

    //new
    Version fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2023, 6, 2));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(fourthTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(fourthTemporalVersion.getComment()).isNull();

    //new
    Version fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(fifthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(fifthTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(fifthTemporalVersion.getComment()).isNull();

  }

  /** Which scenario?
   * NEU:       |______________________|
   * IST:          |-------------------|----------------------|
   * Version:               1                   2
   *
   * RESULTAT:  |----------------------|---------------------|
   * Version:               1                   2
   */
  @Test
  public void validFromEditedIsBeforeTheCurrentValidFrom() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");
    editedVersion.setValidFrom(LocalDate.of(2019, 6, 1));

    //when
    versionService.updateVersion(version1, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(2);
    result.sort(Comparator.comparing(Version::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    Version secondTemporalVersion = result.get(0);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2019, 6, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");

    Version thirdTemporalVersion = result.get(1);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(thirdTemporalVersion.getName()).isEqualTo("FPFN Name");
  }

  /** Which scenario?
   * NEU:                |__________|
   * IST:       |-------------------|----------------------|
   * Version:         1                 2
   *
   * RESULTAT: |---------|__________|---------------------|
   * Version:        1        3         2
   */
  @Test
  public void shouldAddNewVersionWhenValidFromIsModified() {
    //given

    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");
    editedVersion.setValidFrom(LocalDate.of(2020, 6, 1));

    //when
    versionService.updateVersion(version1, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(3);
    result.sort(Comparator.comparing(Version::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2020, 5, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name");

    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 6, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");

    Version thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(thirdTemporalVersion.getName()).isEqualTo("FPFN Name");

  }




}