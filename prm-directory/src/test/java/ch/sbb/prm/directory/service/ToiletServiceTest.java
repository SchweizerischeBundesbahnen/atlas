package ch.sbb.prm.directory.service;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.TOILET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.ToiletTestData;
import ch.sbb.prm.directory.controller.model.PrmObjectRequestParams;
import ch.sbb.prm.directory.entity.*;
import ch.sbb.prm.directory.exception.ElementTypeDoesNotExistException;
import ch.sbb.prm.directory.exception.StopPointDoesNotExistException;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.repository.ToiletRepository;
import ch.sbb.prm.directory.search.ToiletSearchRestrictions;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

class ToiletServiceTest extends BasePrmServiceTest {

  private final ToiletService toiletService;
  private final ToiletRepository toiletRepository;
  private final RelationRepository relationRepository;
  private final StopPointRepository stopPointRepository;
  private final ReferencePointRepository referencePointRepository;

  @Autowired
  ToiletServiceTest(ToiletService toiletService,
      ToiletRepository toiletRepository,
      RelationRepository relationRepository,
      StopPointRepository stopPointRepository,
      ReferencePointRepository referencePointRepository,
      SharedServicePointRepository sharedServicePointRepository,
      PrmLocationService prmLocationService) {
    super(sharedServicePointRepository, prmLocationService);
    this.toiletService = toiletService;
    this.toiletRepository = toiletRepository;
    this.relationRepository = relationRepository;
    this.stopPointRepository = stopPointRepository;
    this.referencePointRepository = referencePointRepository;
  }

  @Test
  void shouldNotCreateToiletWhenStopPointDoesNotExist() {
    //given
    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    toiletVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when & then
    assertThrows(StopPointDoesNotExistException.class,
        () -> toiletService.createToilet(toiletVersion)).getLocalizedMessage();
  }

  @Test
  void shouldFindByParentSloid() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    String parentServicePointSloid = stopPointVersion.getSloid();
    toiletVersion.setParentServicePointSloid(parentServicePointSloid);
    toiletService.save(toiletVersion);
    //when
    Page<ToiletVersion> result = toiletService.findAll(
        ToiletSearchRestrictions.builder().pageable(Pageable.ofSize(1)).prmObjectRequestParams(
            PrmObjectRequestParams.builder().parentServicePointSloids(List.of(parentServicePointSloid)).build()).build());
    //then
    assertThat(result.getTotalElements()).isOne();
    assertThat(result.getContent()).isNotEmpty();
  }

  @Test
  void shouldFindBySloid() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    String parentServicePointSloid = stopPointVersion.getSloid();
    toiletVersion.setParentServicePointSloid(parentServicePointSloid);
    toiletService.save(toiletVersion);
    //when
    Page<ToiletVersion> result = toiletService.findAll(
        ToiletSearchRestrictions.builder().pageable(Pageable.ofSize(1)).prmObjectRequestParams(
            PrmObjectRequestParams.builder().sloids(List.of(toiletVersion.getSloid())).build()).build());
    //then
    assertThat(result.getTotalElements()).isOne();
    assertThat(result.getContent()).isNotEmpty();
  }

  @Test
  void shouldNotFindByWrongParentSloid() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    String parentServicePointSloid = stopPointVersion.getSloid();
    toiletVersion.setParentServicePointSloid(parentServicePointSloid);
    toiletService.save(toiletVersion);
    //when
    Page<ToiletVersion> result = toiletService.findAll(
        ToiletSearchRestrictions.builder().pageable(Pageable.ofSize(1)).prmObjectRequestParams(
            PrmObjectRequestParams.builder().parentServicePointSloids(List.of("ch:1:sloid:metallica")).build()).build());
    //then
    assertThat(result.getTotalElements()).isZero();
  }

  @Test
  void shouldNotFindWithWrongSloid() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    String parentServicePointSloid = stopPointVersion.getSloid();
    toiletVersion.setParentServicePointSloid(parentServicePointSloid);
    toiletService.save(toiletVersion);
    //when
    Page<ToiletVersion> result = toiletService.findAll(
        ToiletSearchRestrictions.builder().pageable(Pageable.ofSize(1)).prmObjectRequestParams(
            PrmObjectRequestParams.builder().sloids(List.of("ch:1:sloid:asd")).build()).build());
    //then
    assertThat(result.getTotalElements()).isZero();
  }

  @Test
  void shouldNotFindByWrongServicePointNumber() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    String parentServicePointSloid = stopPointVersion.getSloid();
    toiletVersion.setParentServicePointSloid(parentServicePointSloid);
    toiletService.save(toiletVersion);
    //when
    Page<ToiletVersion> result = toiletService.findAll(
        ToiletSearchRestrictions.builder().pageable(Pageable.ofSize(1)).prmObjectRequestParams(
            PrmObjectRequestParams.builder().servicePointNumber(8500700).build()).build());
    //then
    assertThat(result.getTotalElements()).isZero();
  }

  @Test
  void shouldNotFindWithWrongServicePointNumber() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    String parentServicePointSloid = stopPointVersion.getSloid();
    toiletVersion.setParentServicePointSloid(parentServicePointSloid);
    toiletService.save(toiletVersion);
    //when
    Page<ToiletVersion> result = toiletService.findAll(
        ToiletSearchRestrictions.builder().pageable(Pageable.ofSize(1)).prmObjectRequestParams(
            PrmObjectRequestParams.builder().servicePointNumbers(List.of(toiletVersion.getNumber().getNumber())).build()).build());
    //then
    assertThat(result.getTotalElements()).isOne();
    assertThat(result.getContent()).isNotEmpty();
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
    verify(prmLocationService, times(1)).allocateSloid(any(), eq(SloidType.TOILET));
  }

  @Test
  void shouldCreateToiletWhenNoReferencePointExists() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    toiletVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when
    toiletService.createToilet(toiletVersion);
    //then
    List<ToiletVersion> toiletVersions = toiletRepository
        .findByParentServicePointSloid(toiletVersion.getParentServicePointSloid());
    assertThat(toiletVersions).hasSize(1);
    assertThat(toiletVersions.get(0).getParentServicePointSloid()).isEqualTo(toiletVersion.getParentServicePointSloid());
    List<RelationVersion> relationVersions = relationRepository.findAllByParentServicePointSloid(
        toiletVersion.getParentServicePointSloid());
    assertThat(relationVersions).isEmpty();
    verify(prmLocationService, times(1)).allocateSloid(any(), eq(SloidType.TOILET));
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

    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    toiletVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when
    toiletService.createToilet(toiletVersion);
    //then
    List<ToiletVersion> toiletVersions = toiletRepository.findByParentServicePointSloid(
        toiletVersion.getParentServicePointSloid());
    assertThat(toiletVersions).hasSize(1);
    assertThat(toiletVersions.get(0).getParentServicePointSloid()).isEqualTo(toiletVersion.getParentServicePointSloid());
    List<RelationVersion> relationVersions = relationRepository.findAllByParentServicePointSloid(
        PARENT_SERVICE_POINT_SLOID);
    assertThat(relationVersions).hasSize(1);
    assertThat(relationVersions.get(0).getParentServicePointSloid()).isEqualTo(PARENT_SERVICE_POINT_SLOID);
    assertThat(relationVersions.get(0).getReferencePointElementType()).isEqualTo(ReferencePointElementType.TOILET);
    verify(prmLocationService, times(1)).allocateSloid(any(), eq(SloidType.TOILET));
  }

  @Test
  void testCheckToiletExists_Exists() {
    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    toiletVersion.setSloid("ch:1:sloid:12345:1");
    toiletRepository.saveAndFlush(toiletVersion);

    assertDoesNotThrow(() -> toiletService.checkToiletExists("ch:1:sloid:12345:1", TOILET.name()));
  }

  @Test
  void testCheckToiletExists_DoesNotExist() {
    assertThrows(ElementTypeDoesNotExistException.class, () -> toiletService.checkToiletExists("ch:1:sloid:12345:1", TOILET.name()));
  }

}
