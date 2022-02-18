package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.line.directory.entity.LineRelation;
import ch.sbb.line.directory.entity.Version;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.repository.VersionRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class VersionServiceScenario2Test extends BaseVersionServiceTest {

  @Autowired
  public VersionServiceScenario2Test(
      VersionRepository versionRepository,
      VersionService versionService) {
    super(versionRepository, versionService);
  }

  /**
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
    editedVersion.setDescription("FPFN Description <changed>");
    editedVersion.setComment("Scenario 2");
    editedVersion.setStatus(Status.ACTIVE);
    editedVersion.setValidFrom(LocalDate.of(2022, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2023, 6, 1));
    editedVersion.getLineRelations()
                 .add(LineRelation.builder().slnid("ch:1:ttfnid:111111").version(version2).build());

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
    assertThat(firstTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("BEX1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.ACTIVE);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

    //updated
    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2022, 5, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(secondTemporalVersion.getComment()).isNull();
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("BEX2");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.ACTIVE);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

    //new
    Version thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 6, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 6, 1));
    assertThat(thirdTemporalVersion.getDescription()).isEqualTo("FPFN Description <changed>");
    assertThat(thirdTemporalVersion.getComment()).isEqualTo("Scenario 2");
    Set<LineRelation> lineRelations = thirdTemporalVersion.getLineRelations();
    assertThat(lineRelations).isNotEmpty();
    assertThat(lineRelations.size()).isEqualTo(1);
    LineRelation lineRelation = lineRelations.stream().iterator().next();
    assertThat(lineRelation).isNotNull();
    assertThat(lineRelation.getSlnid()).isEqualTo("ch:1:ttfnid:111111");
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("BEX2");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.ACTIVE);
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(thirdTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

    //new
    Version fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2023, 6, 2));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(fourthTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(fourthTemporalVersion.getComment()).isNull();
    assertThat(fourthTemporalVersion.getLineRelations()).isEmpty();
    assertThat(fourthTemporalVersion.getNumber()).isEqualTo("BEX2");
    assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.ACTIVE);
    assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(fourthTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

    //current
    Version fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(fifthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(fifthTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(fifthTemporalVersion.getComment()).isNull();
    assertThat(fifthTemporalVersion.getLineRelations()).isEmpty();
    assertThat(fifthTemporalVersion.getNumber()).isEqualTo("BEX3");
    assertThat(fifthTemporalVersion.getStatus()).isEqualTo(Status.ACTIVE);
    assertThat(fifthTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(fifthTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

  }

}
