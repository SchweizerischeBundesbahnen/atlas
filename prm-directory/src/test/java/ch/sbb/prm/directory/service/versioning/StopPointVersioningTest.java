package ch.sbb.prm.directory.service.versioning;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.SharedServicePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.BasePrmImportEntity.Fields;
import ch.sbb.prm.directory.entity.SharedServicePoint;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.service.StopPointService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class StopPointVersioningTest {

  private final StopPointService stopPointService;
  private final StopPointRepository stopPointRepository;
  private final SharedServicePointRepository sharedServicePointRepository;

  @Autowired
  StopPointVersioningTest(StopPointService stopPointService,
                          StopPointRepository stopPointRepository,
                          SharedServicePointRepository sharedServicePointRepository) {
    this.stopPointService = stopPointService;
    this.stopPointRepository = stopPointRepository;
    this.sharedServicePointRepository = sharedServicePointRepository;
  }

  @BeforeEach
  public void init(){
    SharedServicePoint servicePoint =
        SharedServicePointTestData.buildSharedServicePoint("ch:1:sloid:12345", Set.of("ch:1:sboid:100602"), Collections.emptySet());
    sharedServicePointRepository.saveAndFlush(servicePoint);
  }

  @AfterEach
  void cleanUp() {
    sharedServicePointRepository.deleteAll();
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
    StopPointVersion version1 = StopPointTestData.builderVersion1().build();
    StopPointVersion version2 = StopPointTestData.builderVersion2().build();
    StopPointVersion savedVersion1 = stopPointRepository.saveAndFlush(version1);
    stopPointRepository.saveAndFlush(version2);

    StopPointVersion editedVersion = StopPointTestData.builderVersion2().build();
    editedVersion.setFreeText("I'm no more Free :-(!!!");
    editedVersion.setMeansOfTransport(Set.of(MeanOfTransport.METRO, MeanOfTransport.TRAIN));
    editedVersion.setAddress("Wylerstrasse 123");
    editedVersion.setZipCode("3014");
    editedVersion.setCity("Bern");
    editedVersion.setAlternativeTransport(StandardAttributeType.TO_BE_COMPLETED);
    editedVersion.setAlternativeTransportCondition("No way dude!!");
    editedVersion.setAssistanceAvailability(StandardAttributeType.YES);
    editedVersion.setAssistanceCondition("No alternative Bro!");
    editedVersion.setAssistanceService(StandardAttributeType.NO);
    editedVersion.setAudioTicketMachine(StandardAttributeType.PARTIALLY);
    editedVersion.setAdditionalInformation("No alternative");
    editedVersion.setDynamicAudioSystem(StandardAttributeType.TO_BE_COMPLETED);
    editedVersion.setDynamicOpticSystem(StandardAttributeType.TO_BE_COMPLETED);
    editedVersion.setInfoTicketMachine("tick");
    editedVersion.setAdditionalInformation("additional");
    editedVersion.setInteroperable(true);
    editedVersion.setUrl("https://www.prm.sbb");
    editedVersion.setVisualInfo(StandardAttributeType.TO_BE_COMPLETED);
    editedVersion.setWheelchairTicketMachine(StandardAttributeType.TO_BE_COMPLETED);
    editedVersion.setAssistanceRequestFulfilled(BooleanOptionalAttributeType.TO_BE_COMPLETED);
    editedVersion.setTicketMachine(BooleanOptionalAttributeType.TO_BE_COMPLETED);
    editedVersion.setCreationDate(version2.getCreationDate());
    editedVersion.setEditionDate(version2.getEditionDate());
    editedVersion.setCreator(version2.getCreator());
    editedVersion.setEditor(version2.getEditor());
    editedVersion.setVersion(version2.getVersion());
    //when
    stopPointService.updateStopPointVersion(version2, editedVersion);
    //then
    List<StopPointVersion> result = stopPointRepository.findAllByNumberOrderByValidFrom(version2.getNumber());
    assertThat(result).isNotNull().hasSize(2);

    StopPointVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(savedVersion1);

    StopPointVersion secondTemporalVersion = result.get(1);
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
    StopPointVersion version1 = StopPointTestData.builderVersion1().build();
    StopPointVersion version2 = StopPointTestData.builderVersion2().build();
    StopPointVersion version3 = StopPointTestData.builderVersion3().build();
    StopPointVersion savedVersion1 = stopPointRepository.saveAndFlush(version1);
    StopPointVersion savedVersion2 = stopPointRepository.saveAndFlush(version2);
    StopPointVersion savedVersion3 = stopPointRepository.saveAndFlush(version3);

    StopPointVersion editedVersion = StopPointTestData.builderVersion2().build();
    editedVersion.setFreeText("I'm no more Free :-(!!!");
    editedVersion.setValidFrom(LocalDate.of(2001, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2002, 6, 1));
    editedVersion.setCreationDate(version2.getCreationDate());
    editedVersion.setEditionDate(version2.getEditionDate());
    editedVersion.setCreator(version2.getCreator());
    editedVersion.setEditor(version2.getEditor());
    editedVersion.setVersion(version2.getVersion());
    //when
    stopPointService.updateStopPointVersion(version2, editedVersion);
    //then
    List<StopPointVersion> result = stopPointRepository.findAllByNumberOrderByValidFrom(version2.getNumber());
    assertThat(result).isNotNull().hasSize(5);

    StopPointVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(savedVersion1);

    StopPointVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2001, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2001, 5, 31));
    assertThat(secondTemporalVersion.getFreeText()).isEqualTo("I am a free text!!!");

    StopPointVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2001, 6, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2002, 6, 1));
    assertThat(thirdTemporalVersion.getFreeText()).isEqualTo("I'm no more Free :-(!!!");

    StopPointVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2002, 6, 2));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2002, 12, 31));
    assertThat(fourthTemporalVersion.getFreeText()).isEqualTo("I am a free text!!!");

    StopPointVersion fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(savedVersion3);
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
    StopPointVersion version1 = StopPointTestData.builderVersion1().build();
    StopPointVersion version2 = StopPointTestData.builderVersion2().build();
    StopPointVersion savedVersion1 = stopPointRepository.saveAndFlush(version1);
    StopPointVersion savedVersion2 = stopPointRepository.saveAndFlush(version2);

    StopPointVersion editedVersion = StopPointTestData.builderVersion2().build();
    editedVersion.setValidTo(LocalDate.of(2001, 12, 31));
    editedVersion.setCreationDate(version2.getCreationDate());
    editedVersion.setEditionDate(version2.getEditionDate());
    editedVersion.setCreator(version2.getCreator());
    editedVersion.setEditor(version2.getEditor());
    editedVersion.setVersion(version2.getVersion());
    //when
    stopPointService.updateStopPointVersion(version2, editedVersion);
    //then
    List<StopPointVersion> result = stopPointRepository.findAllByNumberOrderByValidFrom(version2.getNumber());
    assertThat(result).isNotNull().hasSize(2);

    StopPointVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(savedVersion1);

    StopPointVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate, Fields.editor, StopPointVersion.Fields.validTo)
        .isEqualTo(savedVersion2);
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2001, 12, 31));
  }

}
