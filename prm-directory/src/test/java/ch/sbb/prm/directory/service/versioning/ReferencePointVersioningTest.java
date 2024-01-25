package ch.sbb.prm.directory.service.versioning;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointAttributeType;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.BasePrmEntityVersion;
import ch.sbb.prm.directory.entity.BasePrmImportEntity.Fields;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.service.BasePrmServiceTest;
import ch.sbb.prm.directory.service.ReferencePointService;
import ch.sbb.prm.directory.service.RelationService;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class ReferencePointVersioningTest extends BasePrmServiceTest {

  private final ReferencePointService referencePointService;
  private final ReferencePointRepository referencePointRepository;
  private final RelationService relationService;
  private final StopPointRepository stopPointRepository;

  @MockBean
  private LocationClient locationClient;

  @Autowired
  ReferencePointVersioningTest(ReferencePointService referencePointService,
      ReferencePointRepository referencePointRepository,
      RelationService relationService,
      StopPointRepository stopPointRepository,
      SharedServicePointRepository sharedServicePointRepository) {
    super(sharedServicePointRepository);
    this.referencePointService = referencePointService;
    this.referencePointRepository = referencePointRepository;
    this.relationService = relationService;
    this.stopPointRepository = stopPointRepository;
  }

  /**
   * Szenario 1a: Update einer bestehenden Version am Ende
   * NEU:                             |________________________________
   * IST:      |----------------------|--------------------------------
   * Version:        1                                2
   * <p>
   * RESULTAT: |----------------------|________________________________
   * Version:        1                                2
   */
  @Test
  void scenario1a() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);

    ReferencePointVersion version1 = ReferencePointTestData.builderVersion1().build();
    version1.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    ReferencePointVersion referencePoint1 = referencePointService.createReferencePoint(version1);

    ReferencePointVersion version2 = ReferencePointTestData.builderVersion2().build();
    version2.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    version2.setVersion(0);
    referencePointService.updateReferencePointVersion(referencePoint1, version2);

    ReferencePointVersion editedVersion = ReferencePointTestData.builderVersion2().build();
    editedVersion.setNumber(ServicePointNumber.ofNumberWithoutCheckDigit(1234567));
    editedVersion.setDesignation("designation never");
    editedVersion.setMainReferencePoint(false);
    editedVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    editedVersion.setReferencePointType(ReferencePointAttributeType.PLATFORM);
    editedVersion.setCreationDate(version2.getCreationDate());
    editedVersion.setEditionDate(version2.getEditionDate());
    editedVersion.setCreator(version2.getCreator());
    editedVersion.setEditor(version2.getEditor());
    editedVersion.setVersion(version2.getVersion());

    //when
    List<ReferencePointVersion> allReferencePointsInDb = referencePointRepository.findAllBySloidOrderByValidFrom(
        version2.getSloid());
    version2.setId(allReferencePointsInDb.get(allReferencePointsInDb.size() - 1).getId());
    referencePointService.updateReferencePointVersion(version2, editedVersion);

    //then
    List<ReferencePointVersion> result = referencePointRepository.findAllBySloidOrderByValidFrom(
        version2.getSloid());
    assertThat(result).isNotNull().hasSize(2);

    ReferencePointVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(referencePoint1);

    ReferencePointVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate, Fields.editor,
            Fields.creator, ReferencePointVersion.Fields.id)
        .isEqualTo(editedVersion);

    List<RelationVersion> relations = relationService.getRelationsByParentServicePointSloid(
        PARENT_SERVICE_POINT_SLOID);
    assertThat(relations).isEmpty();
    verify(locationClient, times(1)).claimSloid(argThat(
        claimSloidRequestModel -> claimSloidRequestModel.sloidType() == SloidType.REFERENCE_POINT
            && Objects.equals(claimSloidRequestModel.sloid(), "ch:1:sloid:12345:1")));
  }

  /**
   * Szenario 2: Update innerhalb existierender Version
   * NEU:                       |___________|
   * IST:      |-----------|----------------------|--------------------
   * Version:        1                 2                  3
   * <p>
   * RESULTAT: |-----------|----|___________|-----|--------------------     NEUE VERSION EINGEFÜGT
   * Version:        1       2         4       5          3
   */
  @Test
  void scenario2() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);

    ReferencePointVersion version1 = ReferencePointTestData.builderVersion1().build();
    version1.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    ReferencePointVersion referencePoint1 = referencePointService.createReferencePoint(version1);

    ReferencePointVersion version2 = ReferencePointTestData.builderVersion2().build();
    version2.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    version2.setVersion(0);
    referencePointService.updateReferencePointVersion(referencePoint1, version2);

    ReferencePointVersion version3 = ReferencePointTestData.builderVersion3().build();
    version3.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    version3.setVersion(0);
    referencePointService.updateReferencePointVersion(version2, version3);

    ReferencePointVersion editedVersion = ReferencePointTestData.builderVersion2().build();
    editedVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    editedVersion.setNumber(ServicePointNumber.ofNumberWithoutCheckDigit(1234567));
    editedVersion.setValidFrom(LocalDate.of(2001, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2002, 6, 1));
    editedVersion.setDesignation("designation never");
    editedVersion.setCreationDate(version2.getCreationDate());
    editedVersion.setEditionDate(version2.getEditionDate());
    editedVersion.setCreator(version2.getCreator());
    editedVersion.setEditor(version2.getEditor());
    editedVersion.setVersion(version2.getVersion());

    //when
    referencePointService.updateReferencePointVersion(version2, editedVersion);

    //then
    List<ReferencePointVersion> result = referencePointRepository.findAllBySloidOrderByValidFrom(
        version2.getSloid());
    assertThat(result).isNotNull().hasSize(5);

    ReferencePointVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(referencePoint1);

    ReferencePointVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2001, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2001, 5, 31));
    assertThat(secondTemporalVersion.getDesignation()).isEqualTo("designation forever");

    ReferencePointVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2001, 6, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2002, 6, 1));
    assertThat(thirdTemporalVersion.getDesignation()).isEqualTo("designation never");

    ReferencePointVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2002, 6, 2));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2002, 12, 31));
    assertThat(fourthTemporalVersion.getDesignation()).isEqualTo("designation forever");

    ReferencePointVersion fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(ReferencePointVersion.Fields.id, Fields.version, Fields.editionDate, Fields.creationDate,
            Fields.editor,
            Fields.creator)
        .isEqualTo(version3);

    List<RelationVersion> relations = relationService.getRelationsByParentServicePointSloid(
        PARENT_SERVICE_POINT_SLOID);
    assertThat(relations).isEmpty();
    verify(locationClient, times(1)).claimSloid(argThat(
        claimSloidRequestModel -> claimSloidRequestModel.sloidType() == SloidType.REFERENCE_POINT
            && Objects.equals(claimSloidRequestModel.sloid(), "ch:1:sloid:12345:1")));
  }

  /**
   * Szenario 8a: Letzte Version terminieren wenn nur validTo ist updated
   * NEU:      |______________________|
   * IST:      |-------------------------------------------------------
   * Version:                            1
   * <p>
   * RESULTAT: |----------------------| Version wird per xx aufgehoben
   * Version:         1
   */
  @Test
  void scenario8a() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);

    ReferencePointVersion version1 = ReferencePointTestData.builderVersion1().build();
    version1.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    ReferencePointVersion referencePoint1 = referencePointService.createReferencePoint(version1);

    ReferencePointVersion version2 = ReferencePointTestData.builderVersion2().build();
    version2.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    version2.setVersion(0);
    referencePointService.updateReferencePointVersion(referencePoint1, version2);

    ReferencePointVersion editedVersion = ReferencePointTestData.builderVersion2().build();
    editedVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    editedVersion.setNumber(ServicePointNumber.ofNumberWithoutCheckDigit(1234567));
    editedVersion.setValidTo(LocalDate.of(2001, 12, 31));
    editedVersion.setCreationDate(version2.getCreationDate());
    editedVersion.setEditionDate(version2.getEditionDate());
    editedVersion.setCreator(version2.getCreator());
    editedVersion.setEditor(version2.getEditor());
    editedVersion.setVersion(version2.getVersion());

    //when
    referencePointService.updateReferencePointVersion(version2, editedVersion);

    //then
    List<ReferencePointVersion> result = referencePointRepository.findAllBySloidOrderByValidFrom(
        version2.getSloid());
    assertThat(result).isNotNull().hasSize(2);

    ReferencePointVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(referencePoint1);

    ReferencePointVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate, Fields.editor,
            Fields.creator, ReferencePointVersion.Fields.id, BasePrmEntityVersion.Fields.validTo)
        .isEqualTo(version2);
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2001, 12, 31));

    List<RelationVersion> relations = relationService.getRelationsByParentServicePointSloid(
        PARENT_SERVICE_POINT_SLOID);
    assertThat(relations).isEmpty();
    verify(locationClient, times(1)).claimSloid(argThat(
        claimSloidRequestModel -> claimSloidRequestModel.sloidType() == SloidType.REFERENCE_POINT
            && Objects.equals(claimSloidRequestModel.sloid(), "ch:1:sloid:12345:1")));
  }

}
