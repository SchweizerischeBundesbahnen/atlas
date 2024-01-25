package ch.sbb.prm.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.api.client.location.LocationClientV1;
import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.ToiletTestData;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.exception.StopPointDoesNotExistException;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.repository.ToiletRepository;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class ToiletServiceTest extends BasePrmServiceTest {

  private final ToiletService toiletService;
  private final ToiletRepository toiletRepository;
  private final RelationRepository relationRepository;
  private final StopPointRepository stopPointRepository;
  private final ReferencePointRepository referencePointRepository;

  @MockBean
  private LocationClientV1 locationClient;

  @Autowired
  ToiletServiceTest(ToiletService toiletService,
      ToiletRepository toiletRepository,
      RelationRepository relationRepository,
      StopPointRepository stopPointRepository,
      ReferencePointRepository referencePointRepository,
      SharedServicePointRepository sharedServicePointRepository) {
    super(sharedServicePointRepository);
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
    verify(locationClient, times(1)).claimSloid(argThat(
        claimSloidRequestModel -> claimSloidRequestModel.sloidType() == SloidType.TOILET
            && Objects.equals(claimSloidRequestModel.sloid(), "ch:1:sloid:12345:1")));
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
    verify(locationClient, times(1)).claimSloid(argThat(
        claimSloidRequestModel -> claimSloidRequestModel.sloidType() == SloidType.TOILET
            && Objects.equals(claimSloidRequestModel.sloid(), "ch:1:sloid:12345:1")));
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
    verify(locationClient, times(1)).claimSloid(argThat(
        claimSloidRequestModel -> claimSloidRequestModel.sloidType() == SloidType.TOILET
            && Objects.equals(claimSloidRequestModel.sloid(), "ch:1:sloid:12345:1")));
  }

}
