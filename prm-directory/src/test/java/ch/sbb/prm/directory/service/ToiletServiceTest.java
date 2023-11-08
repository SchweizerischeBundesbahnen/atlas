package ch.sbb.prm.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.ToiletTestData;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.exception.StopPointDoesNotExistsException;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.repository.ToiletRepository;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.AbstractComparableAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class ToiletServiceTest {

  private final ReferencePointRepository referencePointRepository;
  private final RelationRepository relationRepository;

  private final StopPointRepository stopPointRepository;
  private final ToiletRepository toiletRepository;

  private final ToiletService toiletService;

  @Autowired
  ToiletServiceTest(ReferencePointRepository referencePointRepository, RelationRepository relationRepository,
      StopPointRepository stopPointRepository, ToiletRepository toiletRepository, ToiletService toiletService) {
    this.referencePointRepository = referencePointRepository;
    this.relationRepository = relationRepository;
    this.stopPointRepository = stopPointRepository;
    this.toiletRepository = toiletRepository;
    this.toiletService = toiletService;
  }

  @Test
  void shouldNotCreateToiletWhenStopPointDoesNotExists() {
    //given
    String parentServicePointSloid = "ch:1:sloid:70000";
    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    toiletVersion.setParentServicePointSloid(parentServicePointSloid);

    //when & then
    assertThrows(StopPointDoesNotExistsException.class,
        () -> toiletService.createToilet(toiletVersion)).getLocalizedMessage();
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
    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    toiletVersion.setParentServicePointSloid(parentServicePointSloid);

    //when & then
    //when
    toiletService.createToilet(toiletVersion);

    //then
    List<ToiletVersion> toiletVersions = toiletRepository.findByParentServicePointSloid(
        toiletVersion.getParentServicePointSloid());
    assertThat(toiletVersions).hasSize(1);
    assertThat(toiletVersions.get(0).getParentServicePointSloid()).isEqualTo(toiletVersion.getParentServicePointSloid());
    List<RelationVersion> relationVersions = relationRepository.findAllByParentServicePointSloid(
        toiletVersion.getParentServicePointSloid());
    assertThat(relationVersions).isEmpty();
  }

  @Test
  void shouldCreateToiletWhenNoReferencePointExists() {
    //given
    String parentServicePointSloid = "ch:1:sloid:70000";
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointRepository.save(stopPointVersion);
    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    toiletVersion.setParentServicePointSloid(parentServicePointSloid);
    //when
    toiletService.createToilet(toiletVersion);

    //then
    List<ToiletVersion> toiletVersions = toiletRepository.findByParentServicePointSloid(
        toiletVersion.getParentServicePointSloid());
    assertThat(toiletVersions).hasSize(1);
    assertThat(toiletVersions.get(0).getParentServicePointSloid()).isEqualTo(toiletVersion.getParentServicePointSloid());
    List<RelationVersion> relationVersions = relationRepository.findAllByParentServicePointSloid(
        toiletVersion.getParentServicePointSloid());
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

    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    toiletVersion.setParentServicePointSloid(parentServicePointSloid);

    //when
    toiletService.createToilet(toiletVersion);

    //then
    List<ToiletVersion> toiletVersions = toiletRepository.findByParentServicePointSloid(
        toiletVersion.getParentServicePointSloid());
    assertThat(toiletVersions).hasSize(1);
    assertThat(toiletVersions.get(0).getParentServicePointSloid()).isEqualTo(toiletVersion.getParentServicePointSloid());
    List<RelationVersion> relationVersions = relationRepository.findAllByParentServicePointSloid(
        parentServicePointSloid);
    assertThat(relationVersions).hasSize(1);
    assertThat(relationVersions.get(0).getParentServicePointSloid()).isEqualTo(parentServicePointSloid);
    AbstractComparableAssert<?, ReferencePointElementType> equalTo = assertThat(
        relationVersions.get(0).getReferencePointElementType()).isEqualTo(ReferencePointElementType.TOILET);
  }

}