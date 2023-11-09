package ch.sbb.prm.directory.service.versioning;

import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.SharedServicePointVersionModel;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.TicketCounterTestData;
import ch.sbb.prm.directory.entity.BasePrmImportEntity.Fields;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.entity.TicketCounterVersion;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.repository.TicketCounterRepository;
import ch.sbb.prm.directory.service.RelationService;
import ch.sbb.prm.directory.service.TicketCounterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@Transactional
class TicketCounterVersioningTest {

  private static final String PARENT_SERVICE_POINT_SLOID = "ch:1:sloid:70000";
  private static final SharedServicePointVersionModel SHARED_SERVICE_POINT_VERSION_MODEL =
          new SharedServicePointVersionModel(PARENT_SERVICE_POINT_SLOID,
                  Collections.singleton("sboid"),
                  Collections.singleton(""));

  private final TicketCounterService ticketCounterService;
  private final TicketCounterRepository ticketCounterRepository;
  private final StopPointRepository stopPointRepository;
  private final ReferencePointRepository referencePointRepository;
  private final RelationService relationService;

  @Autowired
  TicketCounterVersioningTest(TicketCounterService ticketCounterService, TicketCounterRepository ticketCounterRepository,
      StopPointRepository stopPointRepository, ReferencePointRepository referencePointRepository,
      RelationService relationService) {
    this.ticketCounterService = ticketCounterService;
    this.ticketCounterRepository = ticketCounterRepository;
    this.stopPointRepository = stopPointRepository;
    this.referencePointRepository = referencePointRepository;
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
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);

    TicketCounterVersion version1 = TicketCounterTestData.builderVersion1().build();
    version1.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    ticketCounterRepository.saveAndFlush(version1);
    TicketCounterVersion version2 = TicketCounterTestData.builderVersion2().build();
    version2.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    ticketCounterRepository.saveAndFlush(version2);

    TicketCounterVersion editedVersion = TicketCounterTestData.builderVersion2().build();
    editedVersion.setNumber(ServicePointNumber.ofNumberWithoutCheckDigit(1234567));
    editedVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    editedVersion.setDesignation("My designation");
    editedVersion.setInductionLoop(StandardAttributeType.NOT_APPLICABLE);
    editedVersion.setOpeningHours("24/7");
    editedVersion.setAdditionalInformation("info");
    editedVersion.setWheelchairAccess(StandardAttributeType.YES);
    editedVersion.setCreationDate(version2.getCreationDate());
    editedVersion.setEditionDate(version2.getEditionDate());
    editedVersion.setCreator(version2.getCreator());
    editedVersion.setEditor(version2.getEditor());
    editedVersion.setVersion(version2.getVersion());
    //when
    ticketCounterService.updateTicketCounterVersion(version2, editedVersion, SHARED_SERVICE_POINT_VERSION_MODEL);

    //then
    List<TicketCounterVersion> result = ticketCounterRepository.findAllByNumberOrderByValidFrom(version2.getNumber());
    assertThat(result).isNotNull().hasSize(2);

    TicketCounterVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(version1);

    TicketCounterVersion secondTemporalVersion = result.get(1);
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
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);

    TicketCounterVersion version1 = TicketCounterTestData.builderVersion1().build();
    version1.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    ticketCounterRepository.saveAndFlush(version1);
    TicketCounterVersion version2 = TicketCounterTestData.builderVersion2().build();
    version2.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    ticketCounterRepository.saveAndFlush(version2);
    TicketCounterVersion version3 = TicketCounterTestData.builderVersion3().build();
    version3.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    ticketCounterRepository.saveAndFlush(version3);

    TicketCounterVersion editedVersion = TicketCounterTestData.builderVersion2().build();
    editedVersion.setNumber(ServicePointNumber.ofNumberWithoutCheckDigit(1234567));
    editedVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    editedVersion.setValidFrom(LocalDate.of(2001, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2002, 6, 1));
    editedVersion.setDesignation("My designation");
    editedVersion.setCreationDate(version2.getCreationDate());
    editedVersion.setEditionDate(version2.getEditionDate());
    editedVersion.setCreator(version2.getCreator());
    editedVersion.setEditor(version2.getEditor());
    editedVersion.setVersion(version2.getVersion());
    //when
    ticketCounterService.updateTicketCounterVersion(version2, editedVersion, SHARED_SERVICE_POINT_VERSION_MODEL);

    //then
    List<TicketCounterVersion> result = ticketCounterRepository.findAllByNumberOrderByValidFrom(version2.getNumber());
    assertThat(result).isNotNull().hasSize(5);

    TicketCounterVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2001, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2001, 5, 31));
    assertThat(secondTemporalVersion.getDesignation()).isEqualTo("Designation Napoli");

    TicketCounterVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2001, 6, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2002, 6, 1));
    assertThat(thirdTemporalVersion.getDesignation()).isEqualTo("My designation");

    TicketCounterVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2002, 6, 2));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2002, 12, 31));
    assertThat(fourthTemporalVersion.getDesignation()).isEqualTo("Designation Napoli");

    TicketCounterVersion fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(version3);

    List<RelationVersion> relations = relationService.getRelationsByParentServicePointSloid(
            PARENT_SERVICE_POINT_SLOID);
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
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);

    TicketCounterVersion version1 = TicketCounterTestData.builderVersion1().build();
    version1.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    ticketCounterRepository.saveAndFlush(version1);
    TicketCounterVersion version2 = TicketCounterTestData.builderVersion2().build();
    version2.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    ticketCounterRepository.saveAndFlush(version2);

    TicketCounterVersion editedVersion = TicketCounterTestData.builderVersion2().build();
    editedVersion.setNumber(ServicePointNumber.ofNumberWithoutCheckDigit(1234567));
    editedVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    editedVersion.setValidTo(LocalDate.of(2001, 12, 31));
    editedVersion.setCreationDate(version2.getCreationDate());
    editedVersion.setEditionDate(version2.getEditionDate());
    editedVersion.setCreator(version2.getCreator());
    editedVersion.setEditor(version2.getEditor());
    editedVersion.setVersion(version2.getVersion());
    //when
    ticketCounterService.updateTicketCounterVersion(version2, editedVersion, SHARED_SERVICE_POINT_VERSION_MODEL);

    //then
    List<TicketCounterVersion> result = ticketCounterRepository.findAllByNumberOrderByValidFrom(version2.getNumber());
    assertThat(result).isNotNull().hasSize(2);

    TicketCounterVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(version1);

    TicketCounterVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate, Fields.editor, StopPointVersion.Fields.validTo)
        .isEqualTo(version2);
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2001, 12, 31));
  }

}