package ch.sbb.prm.directory.service.versioning;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.prm.directory.ParkingLotTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPlaceTestData;
import ch.sbb.prm.directory.entity.BasePrmImportEntity.Fields;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import ch.sbb.prm.directory.service.ParkingLotService;
import ch.sbb.prm.directory.service.RelationService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class ParkingLotVersioningTest {

  private final ReferencePointRepository referencePointRepository;

  private final StopPlaceRepository stopPlaceRepository;

  private final ParkingLotService parkingLotService;
  private final ParkingLotRepository parkingLotRepository;
  private final RelationService relationService;

  @Autowired
  ParkingLotVersioningTest(ReferencePointRepository referencePointRepository,
      StopPlaceRepository stopPlaceRepository, ParkingLotService parkingLotService, ParkingLotRepository parkingLotRepository,
      RelationService relationService) {
    this.referencePointRepository = referencePointRepository;
    this.stopPlaceRepository = stopPlaceRepository;
    this.parkingLotService = parkingLotService;
    this.parkingLotRepository = parkingLotRepository;
    this.relationService = relationService;
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
    StopPlaceVersion stopPlaceVersion = StopPlaceTestData.getStopPlaceVersion();
    stopPlaceVersion.setSloid(parentServicePointSloid);
    stopPlaceRepository.save(stopPlaceVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(parentServicePointSloid);
    referencePointRepository.save(referencePointVersion);
    ParkingLotVersion version1 = ParkingLotTestData.builderVersion1().build();
    version1.setParentServicePointSloid(parentServicePointSloid);
    parkingLotRepository.saveAndFlush(version1);
    ParkingLotVersion version2 = ParkingLotTestData.builderVersion2().build();
    version2.setParentServicePointSloid(parentServicePointSloid);
    parkingLotRepository.saveAndFlush(version2);

    ParkingLotVersion editedVersion = ParkingLotTestData.builderVersion2().build();
    editedVersion.setParentServicePointSloid(parentServicePointSloid);
    editedVersion.setDesignation("hop yb!");
    editedVersion.setInfo("No Info");
    editedVersion.setPlacesAvailable(BooleanOptionalAttributeType.NO);
    editedVersion.setPrmPlacesAvailable(BooleanOptionalAttributeType.NO);
    editedVersion.setCreationDate(version2.getCreationDate());
    editedVersion.setEditionDate(version2.getEditionDate());
    editedVersion.setCreator(version2.getCreator());
    editedVersion.setEditor(version2.getEditor());
    editedVersion.setVersion(version2.getVersion());

    //when
    parkingLotService.updateParkingLotVersion(version2,editedVersion);

    //then
    List<ParkingLotVersion> result = parkingLotRepository.findAllByNumberOrderByValidFrom(version2.getNumber());
    assertThat(result).isNotNull().hasSize(2);

    ParkingLotVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(version1);

    ParkingLotVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate, Fields.editor, StopPlaceVersion.Fields.id)
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
    StopPlaceVersion stopPlaceVersion = StopPlaceTestData.getStopPlaceVersion();
    stopPlaceVersion.setSloid(parentServicePointSloid);
    stopPlaceRepository.save(stopPlaceVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(parentServicePointSloid);
    referencePointRepository.save(referencePointVersion);
    ParkingLotVersion version1 = ParkingLotTestData.builderVersion1().build();
    version1.setParentServicePointSloid(parentServicePointSloid);
    parkingLotRepository.saveAndFlush(version1);
    ParkingLotVersion version2 = ParkingLotTestData.builderVersion2().build();
    version2.setParentServicePointSloid(parentServicePointSloid);
    parkingLotRepository.saveAndFlush(version2);
    ParkingLotVersion version3 = ParkingLotTestData.builderVersion3().build();
    version3.setParentServicePointSloid(parentServicePointSloid);
    parkingLotRepository.saveAndFlush(version3);

    ParkingLotVersion editedVersion = ParkingLotTestData.builderVersion2().build();
    editedVersion.setParentServicePointSloid(parentServicePointSloid);
    editedVersion.setValidFrom(LocalDate.of(2001, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2002, 6, 1));
    editedVersion.setDesignation("hop yb!");
    editedVersion.setPlacesAvailable(BooleanOptionalAttributeType.NO);
    editedVersion.setPrmPlacesAvailable(BooleanOptionalAttributeType.NO);
    editedVersion.setCreationDate(version2.getCreationDate());
    editedVersion.setEditionDate(version2.getEditionDate());
    editedVersion.setCreator(version2.getCreator());
    editedVersion.setEditor(version2.getEditor());
    editedVersion.setVersion(version2.getVersion());

    parkingLotService.updateParkingLotVersion(version2,editedVersion);

    //then
    List<ParkingLotVersion> result = parkingLotRepository.findAllByNumberOrderByValidFrom(version2.getNumber());
    assertThat(result).isNotNull().hasSize(5);

    ParkingLotVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(version1);

    ParkingLotVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2001, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2001, 5, 31));
    assertThat(secondTemporalVersion.getDesignation()).isEqualTo("Designation wrong");

    ParkingLotVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2001, 6, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2002, 6, 1));
    assertThat(thirdTemporalVersion.getDesignation()).isEqualTo("hop yb!");

    ParkingLotVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2002, 6, 2));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2002, 12, 31));
    assertThat(fourthTemporalVersion.getDesignation()).isEqualTo("Designation wrong");

    ParkingLotVersion fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(version3);

    List<RelationVersion> relations = relationService.getRelationsByParentServicePointSloid(
        parentServicePointSloid);
    assertThat(relations).isEmpty();

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
    String parentServicePointSloid = "ch:1:sloid:70000";
    StopPlaceVersion stopPlaceVersion = StopPlaceTestData.getStopPlaceVersion();
    stopPlaceVersion.setSloid(parentServicePointSloid);
    stopPlaceRepository.save(stopPlaceVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(parentServicePointSloid);
    referencePointRepository.save(referencePointVersion);
    ParkingLotVersion version1 = ParkingLotTestData.builderVersion1().build();
    version1.setParentServicePointSloid(parentServicePointSloid);
    parkingLotRepository.saveAndFlush(version1);
    ParkingLotVersion version2 = ParkingLotTestData.builderVersion2().build();
    version2.setParentServicePointSloid(parentServicePointSloid);
    parkingLotRepository.saveAndFlush(version2);

    ParkingLotVersion editedVersion = ParkingLotTestData.builderVersion2().build();
    editedVersion.setParentServicePointSloid(parentServicePointSloid);
    editedVersion.setValidTo(LocalDate.of(2001, 12, 31));
    editedVersion.setCreationDate(version2.getCreationDate());
    editedVersion.setEditionDate(version2.getEditionDate());
    editedVersion.setCreator(version2.getCreator());
    editedVersion.setEditor(version2.getEditor());
    editedVersion.setVersion(version2.getVersion());

    //when
    parkingLotService.updateParkingLotVersion(version2,editedVersion);

    //then
    List<ParkingLotVersion> result = parkingLotRepository.findAllByNumberOrderByValidFrom(version2.getNumber());
    assertThat(result).isNotNull().hasSize(2);

    ParkingLotVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(version1);

    ParkingLotVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate, Fields.editor, StopPlaceVersion.Fields.validTo)
        .isEqualTo(version2);
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2001, 12, 31));
  }

}