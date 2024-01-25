package ch.sbb.prm.directory.service.versioning;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.enumeration.TactileVisualAttributeType;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.RelationTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.BasePrmImportEntity.Fields;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.service.BasePrmServiceTest;
import ch.sbb.prm.directory.service.PrmLocationService;
import ch.sbb.prm.directory.service.RelationService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RelationVersioningTest extends BasePrmServiceTest {

  private final RelationService relationService;
  private final RelationRepository relationRepository;
  private final StopPointRepository stopPointRepository;

  @Autowired
  RelationVersioningTest(RelationService relationService,
                         RelationRepository relationRepository,
                         StopPointRepository stopPointRepository,
                         SharedServicePointRepository sharedServicePointRepository,
      PrmLocationService prmLocationService) {
    super(sharedServicePointRepository, prmLocationService);
    this.relationService = relationService;
    this.relationRepository = relationRepository;
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
  void scenario1a() {
    //given
    String parentServicePointSloid = "ch:1:sloid:70000";
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointRepository.save(stopPointVersion);
    RelationVersion version1 = RelationTestData.builderVersion1().build();
    version1.setParentServicePointSloid(parentServicePointSloid);
    relationRepository.saveAndFlush(version1);
    RelationVersion version2 = RelationTestData.builderVersion2().build();
    version2.setParentServicePointSloid(parentServicePointSloid);
    relationRepository.saveAndFlush(version2);

    RelationVersion editedVersion = RelationTestData.builderVersion2().build();
    editedVersion.setNumber(ServicePointNumber.ofNumberWithoutCheckDigit(1234567));
    editedVersion.setParentServicePointSloid(parentServicePointSloid);
    editedVersion.setContrastingAreas(StandardAttributeType.TO_BE_COMPLETED);
    editedVersion.setReferencePointElementType(ReferencePointElementType.PARKING_LOT);
    editedVersion.setStepFreeAccess(StepFreeAccessAttributeType.YES_WITH_LIFT);
    editedVersion.setTactileVisualMarks(TactileVisualAttributeType.PARTIALLY);
    editedVersion.setCreationDate(version2.getCreationDate());
    editedVersion.setEditionDate(version2.getEditionDate());
    editedVersion.setCreator(version2.getCreator());
    editedVersion.setEditor(version2.getEditor());
    editedVersion.setVersion(version2.getVersion());

    //when
    relationService.updateRelationVersion(version2, editedVersion);

    //then
    List<RelationVersion> result = relationRepository.findAllBySloidOrderByValidFrom(version2.getSloid());
    assertThat(result).isNotNull().hasSize(2);

    RelationVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(version1);

    RelationVersion secondTemporalVersion = result.get(1);
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
  void scenario2() {
    //given
    String parentServicePointSloid = "ch:1:sloid:70000";
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointRepository.save(stopPointVersion);
    RelationVersion version1 = RelationTestData.builderVersion1().build();
    version1.setParentServicePointSloid(parentServicePointSloid);
    relationRepository.saveAndFlush(version1);
    RelationVersion version2 = RelationTestData.builderVersion2().build();
    version2.setParentServicePointSloid(parentServicePointSloid);
    relationRepository.saveAndFlush(version2);
    RelationVersion version3 = RelationTestData.builderVersion3().build();
    version3.setParentServicePointSloid(parentServicePointSloid);
    relationRepository.saveAndFlush(version3);

    RelationVersion editedVersion = RelationTestData.builderVersion2().build();
    editedVersion.setNumber(ServicePointNumber.ofNumberWithoutCheckDigit(1234567));
    editedVersion.setParentServicePointSloid(parentServicePointSloid);
    editedVersion.setValidFrom(LocalDate.of(2001, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2002, 6, 1));
    editedVersion.setStepFreeAccess(StepFreeAccessAttributeType.YES_WITH_LIFT);
    editedVersion.setCreationDate(version2.getCreationDate());
    editedVersion.setEditionDate(version2.getEditionDate());
    editedVersion.setCreator(version2.getCreator());
    editedVersion.setEditor(version2.getEditor());
    editedVersion.setVersion(version2.getVersion());

    //when
    relationService.updateRelationVersion(version2, editedVersion);

    //then
    List<RelationVersion> result = relationRepository.findAllBySloidOrderByValidFrom(version2.getSloid());
    assertThat(result).isNotNull().hasSize(5);

    RelationVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(version1);

    RelationVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2001, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2001, 5, 31));
    assertThat(secondTemporalVersion.getStepFreeAccess()).isEqualTo(StepFreeAccessAttributeType.NO);

    RelationVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2001, 6, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2002, 6, 1));
    assertThat(thirdTemporalVersion.getStepFreeAccess()).isEqualTo(StepFreeAccessAttributeType.YES_WITH_LIFT);

    RelationVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2002, 6, 2));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2002, 12, 31));
    assertThat(fourthTemporalVersion.getStepFreeAccess()).isEqualTo(StepFreeAccessAttributeType.NO);

    RelationVersion fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(version3);

  }

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
    String parentServicePointSloid = "ch:1:sloid:70000";
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointRepository.save(stopPointVersion);
    RelationVersion version1 = RelationTestData.builderVersion1().build();
    version1.setParentServicePointSloid(parentServicePointSloid);
    relationRepository.saveAndFlush(version1);
    RelationVersion version2 = RelationTestData.builderVersion2().build();
    version2.setParentServicePointSloid(parentServicePointSloid);
    relationRepository.saveAndFlush(version2);

    RelationVersion editedVersion = RelationTestData.builderVersion2().build();
    editedVersion.setNumber(ServicePointNumber.ofNumberWithoutCheckDigit(1234567));
    editedVersion.setParentServicePointSloid(parentServicePointSloid);
    editedVersion.setValidTo(LocalDate.of(2001, 12, 31));
    editedVersion.setCreationDate(version2.getCreationDate());
    editedVersion.setEditionDate(version2.getEditionDate());
    editedVersion.setCreator(version2.getCreator());
    editedVersion.setEditor(version2.getEditor());
    editedVersion.setVersion(version2.getVersion());

    //when
    relationService.updateRelationVersion(version2, editedVersion);

    //then
    List<RelationVersion> result = relationRepository.findAllBySloidOrderByValidFrom(version2.getSloid());
    assertThat(result).isNotNull().hasSize(2);

    RelationVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(version1);

    RelationVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate, Fields.editor, StopPointVersion.Fields.validTo)
        .isEqualTo(version2);
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2001, 12, 31));
  }

}