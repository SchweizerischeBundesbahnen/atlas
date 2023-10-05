package ch.sbb.prm.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPlaceTestData;
import ch.sbb.prm.directory.ToiletTestData;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import ch.sbb.prm.directory.repository.ToiletRepository;
import java.util.List;
import org.assertj.core.api.AbstractComparableAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class ToiletServiceTest {

  private final ReferencePointRepository referencePointRepository;
  private final RelationRepository relationRepository;

  private final StopPlaceRepository stopPlaceRepository;
  private final ToiletRepository toiletRepository;

  private final ToiletService toiletService;

  @Autowired
  ToiletServiceTest(ReferencePointRepository referencePointRepository, RelationRepository relationRepository,
      StopPlaceRepository stopPlaceRepository, ToiletRepository toiletRepository, ToiletService toiletService) {
    this.referencePointRepository = referencePointRepository;
    this.relationRepository = relationRepository;
    this.stopPlaceRepository = stopPlaceRepository;
    this.toiletRepository = toiletRepository;
    this.toiletService = toiletService;
  }

  @Test
  void shouldNotCreateToiletWhenStopPlaceDoesNotExists() {
    //given
    String parentServicePointSloid="ch:1:sloid:70000";
    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    toiletVersion.setParentServicePointSloid(parentServicePointSloid);

    //when & then
    String message = assertThrows(IllegalStateException.class,
        () -> toiletService.createToilet(toiletVersion)).getLocalizedMessage();
    assertThat(message).isEqualTo("StopPlace with sloid [ch:1:sloid:70000] does not exists!");
  }

  @Test
  void shouldCreateToiletWhenNoReferencePointExists() {
    //given
    String parentServicePointSloid="ch:1:sloid:70000";
    StopPlaceVersion stopPlaceVersion = StopPlaceTestData.getStopPlaceVersion();
    stopPlaceVersion.setSloid(parentServicePointSloid);
    stopPlaceRepository.save(stopPlaceVersion);
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
    StopPlaceVersion stopPlaceVersion = StopPlaceTestData.getStopPlaceVersion();
    stopPlaceVersion.setSloid(parentServicePointSloid);
    stopPlaceRepository.save(stopPlaceVersion);
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