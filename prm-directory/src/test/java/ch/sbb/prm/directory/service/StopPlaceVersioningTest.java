package ch.sbb.prm.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.StopPlaceTestData;
import ch.sbb.prm.directory.entity.BasePrmImportEntity.Fields;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class StopPlaceVersioningTest {

  private final StopPlaceRepository stopPlaceRepository;
  private final StopPlaceService stopPlaceService;


  @Autowired
  StopPlaceVersioningTest(StopPlaceRepository stopPlaceRepository, StopPlaceService stopPlaceService) {
    this.stopPlaceRepository = stopPlaceRepository;
    this.stopPlaceService = stopPlaceService;
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
    StopPlaceVersion version1 = StopPlaceTestData.builderVersion1().build();
    StopPlaceVersion version2 = StopPlaceTestData.builderVersion2().build();
    StopPlaceVersion savedVersion1 = stopPlaceRepository.saveAndFlush(version1);
    stopPlaceRepository.saveAndFlush(version2);

    StopPlaceVersion editedVersion = StopPlaceTestData.builderVersion2().build();
    editedVersion.setFreeText("I'm no more Free :-(!!!");
    editedVersion.setMeansOfTransport(Set.of(MeanOfTransport.METRO, MeanOfTransport.CABLE_CAR));
    editedVersion.setAddress("Wylerstrasse 123");
    editedVersion.setZipCode("3014");
    editedVersion.setCity("Bern");
    editedVersion.setAlternativeTransport(StandardAttributeType.TO_BE_COMPLETED);
    editedVersion.setAlternativeTransportCondition("No way dude!!");
    editedVersion.setAssistanceAvailability(StandardAttributeType.YES);
    editedVersion.setAlternativeCondition("No alternative Bro!");
    editedVersion.setAssistanceService(StandardAttributeType.NO);
    editedVersion.setAudioTicketMachine(StandardAttributeType.PARTIALLY);
    editedVersion.setAdditionalInfo("No alternative");
    editedVersion.setDynamicAudioSystem(StandardAttributeType.TO_BE_COMPLETED);
    editedVersion.setDynamicOpticSystem(StandardAttributeType.TO_BE_COMPLETED);
    editedVersion.setInfoTicketMachine("tick");
    editedVersion.setAdditionalInfo("additional");
    editedVersion.setInteroperable(true);
    editedVersion.setUrl("https://www.prm.sbb");
    editedVersion.setVisualInfo(StandardAttributeType.TO_BE_COMPLETED);
    editedVersion.setWheelchairTicketMachine(StandardAttributeType.TO_BE_COMPLETED);
    editedVersion.setAssistanceRequestFulfilled(StandardAttributeType.TO_BE_COMPLETED);
    editedVersion.setTicketMachine(StandardAttributeType.TO_BE_COMPLETED);
    editedVersion.setCreationDate(version2.getCreationDate());
    editedVersion.setEditionDate(version2.getEditionDate());
    editedVersion.setCreator(version2.getCreator());
    editedVersion.setEditor(version2.getEditor());
    editedVersion.setVersion(version2.getVersion());

    //when
    stopPlaceService.updateStopPlaceVersion(version2, editedVersion);
    //then
    List<StopPlaceVersion> result = stopPlaceRepository.findAllByNumberOrderByValidFrom(version2.getNumber());
    assertThat(result).isNotNull().hasSize(2);

    StopPlaceVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(savedVersion1);

    StopPlaceVersion secondTemporalVersion = result.get(1);
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
  void scenario2a() {
    //given
    StopPlaceVersion version1 = StopPlaceTestData.builderVersion1().build();
    StopPlaceVersion version2 = StopPlaceTestData.builderVersion2().build();
    StopPlaceVersion version3 = StopPlaceTestData.builderVersion3().build();
    StopPlaceVersion savedVersion1 = stopPlaceRepository.saveAndFlush(version1);
    StopPlaceVersion savedVersion2 = stopPlaceRepository.saveAndFlush(version2);
    StopPlaceVersion savedVersion3 = stopPlaceRepository.saveAndFlush(version3);

    StopPlaceVersion editedVersion = StopPlaceTestData.builderVersion2().build();
    editedVersion.setFreeText("I'm no more Free :-(!!!");
    editedVersion.setValidFrom(LocalDate.of(2001, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2002, 6, 1));
    editedVersion.setCreationDate(version2.getCreationDate());
    editedVersion.setEditionDate(version2.getEditionDate());
    editedVersion.setCreator(version2.getCreator());
    editedVersion.setEditor(version2.getEditor());
    editedVersion.setVersion(version2.getVersion());


    //when
    stopPlaceService.updateStopPlaceVersion(version2, editedVersion);
    //then
    List<StopPlaceVersion> result = stopPlaceRepository.findAllByNumberOrderByValidFrom(version2.getNumber());
    assertThat(result).isNotNull().hasSize(5);

    StopPlaceVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(savedVersion1);

    StopPlaceVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2001, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2001, 5, 31));
    assertThat(secondTemporalVersion.getFreeText()).isEqualTo("I am a free text!!!");

    StopPlaceVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2001, 6, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2002, 6, 1));
    assertThat(thirdTemporalVersion.getFreeText()).isEqualTo("I'm no more Free :-(!!!");

    StopPlaceVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2002, 6, 2));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2002, 12, 31));
    assertThat(fourthTemporalVersion.getFreeText()).isEqualTo("I am a free text!!!");

    StopPlaceVersion fifthTemporalVersion = result.get(4);
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
    StopPlaceVersion version1 = StopPlaceTestData.builderVersion1().build();
    StopPlaceVersion version2 = StopPlaceTestData.builderVersion2().build();
    StopPlaceVersion savedVersion1 = stopPlaceRepository.saveAndFlush(version1);
    StopPlaceVersion savedVersion2 = stopPlaceRepository.saveAndFlush(version2);

    StopPlaceVersion editedVersion = StopPlaceTestData.builderVersion2().build();
    editedVersion.setValidTo(LocalDate.of(2001, 12, 31));
    editedVersion.setCreationDate(version2.getCreationDate());
    editedVersion.setEditionDate(version2.getEditionDate());
    editedVersion.setCreator(version2.getCreator());
    editedVersion.setEditor(version2.getEditor());
    editedVersion.setVersion(version2.getVersion());

    //when
    stopPlaceService.updateStopPlaceVersion(version2, editedVersion);
    //then
    List<StopPlaceVersion> result = stopPlaceRepository.findAllByNumberOrderByValidFrom(version2.getNumber());
    assertThat(result).isNotNull().hasSize(2);

    StopPlaceVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate)
        .isEqualTo(savedVersion1);

    StopPlaceVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion)
        .usingRecursiveComparison()
        .ignoringFields(Fields.version, Fields.editionDate, Fields.creationDate, Fields.editor, StopPlaceVersion.Fields.validTo)
        .isEqualTo(savedVersion2);
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2001, 12, 31));

  }

}
