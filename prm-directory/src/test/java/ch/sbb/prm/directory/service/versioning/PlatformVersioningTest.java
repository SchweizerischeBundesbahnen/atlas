package ch.sbb.prm.directory.service.versioning;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.BasicAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.prm.directory.PlatformTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.BasePrmImportEntity.Fields;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.service.BasePrmServiceTest;
import ch.sbb.prm.directory.service.PlatformService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PlatformVersioningTest extends BasePrmServiceTest {

  private final PlatformService platformService;
  private final PlatformRepository platformRepository;
  private final StopPointRepository stopPointRepository;

  @Autowired
  PlatformVersioningTest(PlatformService platformService,
                         PlatformRepository platformRepository,
                         StopPointRepository stopPointRepository,
                         SharedServicePointRepository sharedServicePointRepository) {
    super(sharedServicePointRepository);
    this.platformService = platformService;
    this.platformRepository = platformRepository;
    this.stopPointRepository = stopPointRepository;
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
  void scenario1aComplete() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    PlatformVersion version1 = PlatformTestData.builderCompleteVersion1().build();
    version1.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    PlatformVersion version2 = PlatformTestData.builderCompleteVersion2().build();
    version2.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    PlatformVersion savedVersion1 = platformRepository.saveAndFlush(version1);
    PlatformVersion savedVersion2 = platformRepository.saveAndFlush(version2);

    PlatformVersion editedVersion = PlatformTestData.builderCompleteVersion2().build();
    editedVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    editedVersion.setBoardingDevice(BoardingDeviceAttributeType.NO);
    editedVersion.setAdviceAccessInfo("No no Access Information Advice");
    editedVersion.setContrastingAreas(BooleanOptionalAttributeType.NO);
    editedVersion.setDynamicAudio(BasicAttributeType.YES);
    editedVersion.setDynamicVisual(BasicAttributeType.TO_BE_COMPLETED);
    editedVersion.setInclination(323.12);
    editedVersion.setInclinationWidth(323.12);
    editedVersion.setAdditionalInformation("no additional");
    editedVersion.setLevelAccessWheelchair(BasicAttributeType.YES);
    editedVersion.setSuperelevation(121.123);
    editedVersion.setCreationDate(version2.getCreationDate());
    editedVersion.setEditionDate(version2.getEditionDate());
    editedVersion.setCreator(version2.getCreator());
    editedVersion.setEditor(version2.getEditor());
    editedVersion.setVersion(version2.getVersion());
    //when
    platformService.updatePlatformVersion(version2,editedVersion);

    //then
    List<PlatformVersion> result = platformRepository.findAllByNumberOrderByValidFrom(version2.getNumber());
    assertThat(result).isNotNull().hasSize(2);

    PlatformVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(savedVersion1);

    PlatformVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate, Fields.editor, StopPointVersion.Fields.id)
        .isEqualTo(editedVersion);
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
  void scenario2a() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    PlatformVersion version1 = PlatformTestData.builderCompleteVersion1().build();
    version1.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    PlatformVersion version2 = PlatformTestData.builderCompleteVersion2().build();
    version2.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    PlatformVersion version3 = PlatformTestData.builderCompleteVersion3().build();
    version3.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    PlatformVersion savedVersion1 = platformRepository.saveAndFlush(version1);
    PlatformVersion savedVersion2 = platformRepository.saveAndFlush(version2);
    PlatformVersion savedVersion3 = platformRepository.saveAndFlush(version3);

    PlatformVersion editedVersion = PlatformTestData.builderCompleteVersion2().build();
    editedVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    editedVersion.setAdditionalInformation("additionalInfo");
    editedVersion.setValidFrom(LocalDate.of(2001, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2002, 6, 1));
    editedVersion.setCreationDate(version2.getCreationDate());
    editedVersion.setEditionDate(version2.getEditionDate());
    editedVersion.setCreator(version2.getCreator());
    editedVersion.setEditor(version2.getEditor());
    editedVersion.setVersion(version2.getVersion());
    //when
    platformService.updatePlatformVersion(version2, editedVersion);
    //then
    List<PlatformVersion> result = platformRepository.findAllByNumberOrderByValidFrom(version2.getNumber());
    assertThat(result).isNotNull().hasSize(5);

    PlatformVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(savedVersion1);

    PlatformVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2001, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2001, 5, 31));
    assertThat(secondTemporalVersion.getAdditionalInformation()).isEqualTo("additional 2");

    PlatformVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2001, 6, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2002, 6, 1));
    assertThat(thirdTemporalVersion.getAdditionalInformation()).isEqualTo("additionalInfo");

    PlatformVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2002, 6, 2));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2002, 12, 31));
    assertThat(fourthTemporalVersion.getAdditionalInformation()).isEqualTo("additional 2" );

    PlatformVersion fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(savedVersion3);
  }
  //TODO: add tests reduced and complete
  /**
   * Szenario 8a: Letzte Version terminieren wenn nur validTo ist updated
   * NEU:      |______________________|
   * IST:      |-------------------------------------------------------
   * Version:                            1
   *
   * RESULTAT: |----------------------| Version wird per xx aufgehoben
   * Version:         1
   */
  @Test
  void scenario8a() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    PlatformVersion version1 = PlatformTestData.builderCompleteVersion1().build();
    version1.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    PlatformVersion version2 = PlatformTestData.builderCompleteVersion2().build();
    version2.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    PlatformVersion savedVersion1 = platformRepository.saveAndFlush(version1);
    PlatformVersion savedVersion2 = platformRepository.saveAndFlush(version2);

    PlatformVersion editedVersion = PlatformTestData.builderCompleteVersion2().build();
    editedVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    editedVersion.setValidTo(LocalDate.of(2001, 12, 31));
    editedVersion.setCreationDate(version2.getCreationDate());
    editedVersion.setEditionDate(version2.getEditionDate());
    editedVersion.setCreator(version2.getCreator());
    editedVersion.setEditor(version2.getEditor());
    editedVersion.setVersion(version2.getVersion());
    //when
    platformService.updatePlatformVersion(version2, editedVersion);
    //then
    List<PlatformVersion> result = platformRepository.findAllByNumberOrderByValidFrom(version2.getNumber());
    assertThat(result).isNotNull().hasSize(2);

    PlatformVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(savedVersion1);

    PlatformVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate, Fields.editor, StopPointVersion.Fields.validTo)
        .isEqualTo(savedVersion2);
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2001, 12, 31));
  }

}