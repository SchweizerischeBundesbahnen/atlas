package ch.sbb.prm.directory.service;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.InformationDeskTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.InformationDeskVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.exception.StopPointDoesNotExistException;
import ch.sbb.prm.directory.repository.InformationDeskRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import org.assertj.core.api.AbstractComparableAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InformationDeskServiceTest extends BasePrmServiceTest {

  private final InformationDeskService informationDeskService;
  private final InformationDeskRepository informationDeskRepository;
  private final RelationRepository relationRepository;
  private final StopPointRepository stopPointRepository;
  private final ReferencePointRepository referencePointRepository;

  @Autowired
  InformationDeskServiceTest(InformationDeskService informationDeskService,
                             InformationDeskRepository informationDeskRepository,
                             RelationRepository relationRepository,
                             StopPointRepository stopPointRepository,
                             ReferencePointRepository referencePointRepository,
                             SharedServicePointRepository sharedServicePointRepository) {
    super(sharedServicePointRepository);
    this.informationDeskService = informationDeskService;
    this.informationDeskRepository = informationDeskRepository;
    this.relationRepository = relationRepository;
    this.stopPointRepository = stopPointRepository;
    this.referencePointRepository = referencePointRepository;
  }

  @Test
  void shouldNotCreateInformationDeskWhenStopPointDoesNotExist() {
    //given
    InformationDeskVersion informationDesk = InformationDeskTestData.getInformationDeskVersion();
    informationDesk.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when & then
    assertThrows(StopPointDoesNotExistException.class,
        () -> informationDeskService.createInformationDesk(informationDesk)).getLocalizedMessage();
  }

  @Test
  void shouldCreateInformationDeskWhenNoReferencePointExists() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    InformationDeskVersion informationDesk = InformationDeskTestData.getInformationDeskVersion();
    informationDesk.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when
    informationDeskService.createInformationDesk(informationDesk);

    //then
    List<InformationDeskVersion> informationDeskVersions = informationDeskRepository
            .findByParentServicePointSloid(informationDesk.getParentServicePointSloid());
    assertThat(informationDeskVersions).hasSize(1);
    assertThat(informationDeskVersions.get(0).getParentServicePointSloid()).isEqualTo(informationDesk.getParentServicePointSloid());
    List<RelationVersion> relationVersions = relationRepository
            .findAllByParentServicePointSloid(informationDesk.getParentServicePointSloid());
    assertThat(relationVersions).isEmpty();
  }

  @Test
  void shouldCreateToiletWhenReferencePointExists() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);
    InformationDeskVersion informationDesk = InformationDeskTestData.getInformationDeskVersion();
    informationDesk.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when
    informationDeskService.createInformationDesk(informationDesk);

    //then
    List<InformationDeskVersion> informationDeskVersions = informationDeskRepository
            .findByParentServicePointSloid(informationDesk.getParentServicePointSloid());
    assertThat(informationDeskVersions).hasSize(1);
    assertThat(informationDeskVersions.get(0).getParentServicePointSloid()).isEqualTo(informationDesk.getParentServicePointSloid());
    List<RelationVersion> relationVersions = relationRepository
            .findAllByParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    assertThat(relationVersions).hasSize(1);
    assertThat(relationVersions.get(0).getParentServicePointSloid()).isEqualTo(PARENT_SERVICE_POINT_SLOID);
    AbstractComparableAssert<?, ReferencePointElementType> equalTo = assertThat(
        relationVersions.get(0).getReferencePointElementType()).isEqualTo(ReferencePointElementType.INFORMATION_DESK);
    assertThat(relationVersions.get(0).getParentServicePointSloid()).isEqualTo(PARENT_SERVICE_POINT_SLOID);
    assertThat(relationVersions.get(0).getReferencePointElementType()).isEqualTo(ReferencePointElementType.INFORMATION_DESK);
  }
  @Test
  void shouldNotCreateToiletRelationWhenStopPointIsReduced() {
    //given
    String parentServicePointSloid = "ch:1:sloid:70000";
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointVersion.setMeansOfTransport(Set.of(MeanOfTransport.BUS));
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
    assertThat(relationVersions).isEmpty();
  }

}