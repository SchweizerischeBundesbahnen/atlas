package ch.sbb.prm.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.ContactPointTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.ContactPointVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.exception.StopPointDoesNotExistException;
import ch.sbb.prm.directory.repository.ContactPointRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.AbstractComparableAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ContactPointServiceTest extends BasePrmServiceTest {

  private final ContactPointService contactPointService;
  private final ContactPointRepository contactPointRepository;
  private final RelationRepository relationRepository;
  private final StopPointRepository stopPointRepository;
  private final ReferencePointRepository referencePointRepository;

  @Autowired
  ContactPointServiceTest(ContactPointService contactPointService,
      ContactPointRepository contactPointRepository,
      RelationRepository relationRepository,
      StopPointRepository stopPointRepository,
      ReferencePointRepository referencePointRepository,
      SharedServicePointRepository sharedServicePointRepository,
      PrmLocationService prmLocationService) {
    super(sharedServicePointRepository, prmLocationService);
    this.contactPointService = contactPointService;
    this.contactPointRepository = contactPointRepository;
    this.relationRepository = relationRepository;
    this.stopPointRepository = stopPointRepository;
    this.referencePointRepository = referencePointRepository;
  }

  @Test
  void shouldNotCreateContactPointWhenStopPointDoesNotExist() {
    //given
    ContactPointVersion contactPointVersion = ContactPointTestData.getContactPointVersion();
    contactPointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when & then
    assertThrows(StopPointDoesNotExistException.class,
        () -> contactPointService.createContactPoint(contactPointVersion)).getLocalizedMessage();
  }

  @Test
  void shouldCreateContactPointWhenNoReferencePointExists() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ContactPointVersion contactPointVersion = ContactPointTestData.getContactPointVersion();
    contactPointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when
    contactPointService.createContactPoint(contactPointVersion);

    //then
    List<ContactPointVersion> contactPointVersions = contactPointRepository
        .findByParentServicePointSloid(contactPointVersion.getParentServicePointSloid());
    assertThat(contactPointVersions).hasSize(1);
    assertThat(contactPointVersions.get(0).getParentServicePointSloid()).isEqualTo(
        contactPointVersion.getParentServicePointSloid());
    List<RelationVersion> relationVersions = relationRepository
        .findAllByParentServicePointSloid(contactPointVersion.getParentServicePointSloid());
    assertThat(relationVersions).isEmpty();
    verify(prmLocationService, times(1)).allocateSloid(any(),eq(SloidType.INFO_DESK));
  }

  @Test
  void shouldCreateInformationDeskWhenReferencePointExists() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);
    ContactPointVersion contactPointVersion = ContactPointTestData.getContactPointVersion();
    contactPointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when
    contactPointService.createContactPoint(contactPointVersion);

    //then
    List<ContactPointVersion> contactPointVersions = contactPointRepository
        .findByParentServicePointSloid(contactPointVersion.getParentServicePointSloid());
    assertThat(contactPointVersions).hasSize(1);
    assertThat(contactPointVersions.get(0).getParentServicePointSloid()).isEqualTo(
        contactPointVersion.getParentServicePointSloid());
    List<RelationVersion> relationVersions = relationRepository
        .findAllByParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    assertThat(relationVersions).hasSize(1);
    assertThat(relationVersions.get(0).getParentServicePointSloid()).isEqualTo(PARENT_SERVICE_POINT_SLOID);
    AbstractComparableAssert<?, ReferencePointElementType> equalTo = assertThat(
        relationVersions.get(0).getReferencePointElementType()).isEqualTo(ReferencePointElementType.CONTACT_POINT);
    assertThat(relationVersions.get(0).getParentServicePointSloid()).isEqualTo(PARENT_SERVICE_POINT_SLOID);
    assertThat(relationVersions.get(0).getReferencePointElementType()).isEqualTo(ReferencePointElementType.CONTACT_POINT);
    verify(prmLocationService, times(1)).allocateSloid(any(),eq(SloidType.INFO_DESK));
  }

  @Test
  void shouldNotCreateInformationDeskRelationWhenStopPointIsReduced() {
    //given
    String parentServicePointSloid = "ch:1:sloid:70000";
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointVersion.setMeansOfTransport(Set.of(MeanOfTransport.BUS));
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(parentServicePointSloid);
    referencePointRepository.save(referencePointVersion);
    ContactPointVersion contactPointVersion = ContactPointTestData.getContactPointVersion();
    contactPointVersion.setParentServicePointSloid(parentServicePointSloid);

    //when
    contactPointService.createContactPoint(contactPointVersion);

    //then
    List<ContactPointVersion> contactPointVersions = contactPointRepository.findByParentServicePointSloid(
            contactPointVersion.getParentServicePointSloid());
    assertThat(contactPointVersions).hasSize(1);
    assertThat(contactPointVersions.get(0).getParentServicePointSloid()).isEqualTo(
        contactPointVersion.getParentServicePointSloid());
    List<RelationVersion> relationVersions = relationRepository.findAllByParentServicePointSloid(
        parentServicePointSloid);
    assertThat(relationVersions).isEmpty();
    verify(prmLocationService, times(1)).allocateSloid(any(),eq(SloidType.INFO_DESK));
  }

}
