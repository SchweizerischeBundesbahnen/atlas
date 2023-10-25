package ch.sbb.prm.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.prm.directory.InformationDeskTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.InformationDeskVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.exception.StopPointDoesNotExistsException;
import ch.sbb.prm.directory.repository.InformationDeskRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import java.util.List;
import org.assertj.core.api.AbstractComparableAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class InformationDeskServiceTest {

  private final ReferencePointRepository referencePointRepository;
  private final RelationRepository relationRepository;

  private final StopPointRepository stopPointRepository;

  private final InformationDeskRepository informationDeskRepository;
  private final InformationDeskService informationDeskService;

  @Autowired
  InformationDeskServiceTest(ReferencePointRepository referencePointRepository, RelationRepository relationRepository,
      StopPointRepository stopPointRepository, InformationDeskRepository informationDeskRepository, InformationDeskService informationDeskService) {
    this.referencePointRepository = referencePointRepository;
    this.relationRepository = relationRepository;
    this.stopPointRepository = stopPointRepository;
    this.informationDeskRepository = informationDeskRepository;
    this.informationDeskService = informationDeskService;
  }

  @Test
  void shouldNotCreateInformationDeskWhenStopPointDoesNotExists() {
    //given
    String parentServicePointSloid = "ch:1:sloid:70000";
    InformationDeskVersion informationDesk = InformationDeskTestData.getInformationDeskVersion();
    informationDesk.setParentServicePointSloid(parentServicePointSloid);

    //when & then
    assertThrows(StopPointDoesNotExistsException.class,
        () -> informationDeskService.createInformationDesk(informationDesk)).getLocalizedMessage();
  }

  @Test
  void shouldCreateInformationDeskWhenNoReferencePointExists() {
    //given
    String parentServicePointSloid = "ch:1:sloid:70000";
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointRepository.save(stopPointVersion);
    InformationDeskVersion informationDesk = InformationDeskTestData.getInformationDeskVersion();
    informationDesk.setParentServicePointSloid(parentServicePointSloid);
    //when
    informationDeskService.createInformationDesk(informationDesk);

    //then
    List<InformationDeskVersion> informationDeskVersions = informationDeskRepository.findByParentServicePointSloid(
        informationDesk.getParentServicePointSloid());
    assertThat(informationDeskVersions).hasSize(1);
    assertThat(informationDeskVersions.get(0).getParentServicePointSloid()).isEqualTo(informationDesk.getParentServicePointSloid());
    List<RelationVersion> relationVersions = relationRepository.findAllByParentServicePointSloid(
        informationDesk.getParentServicePointSloid());
    assertThat(relationVersions).isEmpty();
  }

  @Test
  void shouldCreateToiletWhenReferencePointExists() {
    //given
    String parentServicePointSloid = "ch:1:sloid:70000";
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(parentServicePointSloid);
    referencePointRepository.save(referencePointVersion);
    InformationDeskVersion informationDesk = InformationDeskTestData.getInformationDeskVersion();
    informationDesk.setParentServicePointSloid(parentServicePointSloid);

    //when
    informationDeskService.createInformationDesk(informationDesk);

    //then
    List<InformationDeskVersion> informationDeskVersions = informationDeskRepository.findByParentServicePointSloid(
        informationDesk.getParentServicePointSloid());
    assertThat(informationDeskVersions).hasSize(1);
    assertThat(informationDeskVersions.get(0).getParentServicePointSloid()).isEqualTo(informationDesk.getParentServicePointSloid());
    List<RelationVersion> relationVersions = relationRepository.findAllByParentServicePointSloid(
        parentServicePointSloid);
    assertThat(relationVersions).hasSize(1);
    assertThat(relationVersions.get(0).getParentServicePointSloid()).isEqualTo(parentServicePointSloid);
    AbstractComparableAssert<?, ReferencePointElementType> equalTo = assertThat(
        relationVersions.get(0).getReferencePointElementType()).isEqualTo(ReferencePointElementType.INFORMATION_DESK);
  }

}