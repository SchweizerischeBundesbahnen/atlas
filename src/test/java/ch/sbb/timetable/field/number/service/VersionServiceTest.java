package ch.sbb.timetable.field.number.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.timetable.field.number.IntegrationTest;
import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.repository.VersionRepository;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import ch.sbb.timetable.field.number.versioning.model.VersioningAction;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class VersionServiceTest {

  private final VersionRepository versionRepository;
  private final VersionService versionService;

  private Version version1;
  private Version version2;

  @BeforeEach
  void init() {
    version1 = Version.builder().ttfnid("ch:1:fpfnid:100000")
                     .name("FPFN Name")
                     .number("BEX")
                     .swissTimetableFieldNumber("b0.BEX")
                     .validFrom(LocalDate.of(2020, 1, 1))
                     .validTo(LocalDate.of(2021, 12, 31))
                     .build();
    version1 = versionRepository.save(version1);
    version2 = Version.builder().ttfnid("ch:1:fpfnid:100000")
                     .name("FPFN Name")
                     .number("BEX")
                     .swissTimetableFieldNumber("b0.BEX")
                     .validFrom(LocalDate.of(2022, 1, 1))
                     .validTo(LocalDate.of(2023, 12, 13))
                     .build();
    version2 = versionRepository.save(version2);
  }

  @AfterEach
  void cleanUp(){
    List<Version> versionsVersioned = versionRepository.getAllVersionsVersioned(
        version1.getTtfnid());
    versionRepository.deleteAll(versionsVersioned);
  }

  @Autowired
  public VersionServiceTest(VersionRepository versionRepository,
      VersionService versionService) {
    this.versionRepository = versionRepository;
    this.versionService = versionService;
  }

  @Test
  public void shouldUpdateVersion(){
    //given
    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");

    //when
    List<VersionedObject> result = versionService.updateVersion(version1, editedVersion);
    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(1);
    assertThat(result.get(0)).isNotNull();
    VersionedObject versionedObject = result.get(0);
    assertThat(versionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
  }

  @Test
  public void shouldAddNewVersion(){
    //given
    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");
    editedVersion.setValidFrom(LocalDate.of(2020, 6, 1));

    //when
    List<VersionedObject> result = versionService.updateVersion(version1, editedVersion);
    //then

    List<Version> allVersionsVersioned = versionRepository.getAllVersionsVersioned(
        version1.getTtfnid());
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(3);
    assertThat(result.get(0)).isNotNull();
    VersionedObject versionedObject = result.get(0);
    assertThat(versionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
  }

}