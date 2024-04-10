package ch.sbb.prm.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.enumeration.TactileVisualAttributeType;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.RelationTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.exception.ReducedVariantException;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RelationServiceTest extends BasePrmServiceTest {

  private final RelationService relationService;
  private final RelationRepository relationRepository;
  private final StopPointRepository stopPointRepository;

  @Autowired
  RelationServiceTest(RelationService relationService,
                      RelationRepository relationRepository,
                      StopPointRepository stopPointRepository,
                      SharedServicePointRepository sharedServicePointRepository,
      PrmLocationService prmLocationService) {
    super(sharedServicePointRepository, prmLocationService);
    this.relationService = relationService;
    this.relationRepository = relationRepository;
    this.stopPointRepository = stopPointRepository;
  }

  @Test
  void shouldNotCreateRelationWhenStopPointIsReduced() {
    //given
    String parentServicePointSloid = "ch:1:sloid:70000";
    StopPointVersion stopPointVersion = StopPointTestData.builderVersion1().meansOfTransport(Set.of(MeanOfTransport.BUS)).build();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointRepository.save(stopPointVersion);
    RelationVersion relationVersion = RelationTestData.builderVersion1().parentServicePointSloid(parentServicePointSloid).build();

    //when
    ReducedVariantException result = Assertions.assertThrows(
        ReducedVariantException.class,
        () -> relationService.save(relationVersion));

    //then
    assertThat(result).isNotNull();
    ErrorResponse errorResponse = result.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(412);
    assertThat(errorResponse.getMessage()).isEqualTo("Object creation not allowed for reduced variant!");
    List<RelationVersion> relations = relationService.getRelationsByParentServicePointSloid(
        parentServicePointSloid);
    assertThat(relations).isEmpty();
  }

  @Test
  void shouldNotUpdateRelationWhenStopPointIsReduced() {
    //given
    String parentServicePointSloid = "ch:1:sloid:70000";
    StopPointVersion stopPointVersion = StopPointTestData.builderVersion1().meansOfTransport(Set.of(MeanOfTransport.BUS)).build();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointRepository.save(stopPointVersion);
    RelationVersion version1 = RelationTestData.builderVersion1().build();
    version1.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    relationRepository.saveAndFlush(version1);
    RelationVersion editedVersion = RelationTestData.builderVersion2().build();
    editedVersion.setNumber(ServicePointNumber.ofNumberWithoutCheckDigit(1234567));
    editedVersion.setParentServicePointSloid(parentServicePointSloid);
    editedVersion.setContrastingAreas(StandardAttributeType.TO_BE_COMPLETED);
    editedVersion.setReferencePointElementType(ReferencePointElementType.PARKING_LOT);
    editedVersion.setStepFreeAccess(StepFreeAccessAttributeType.YES_WITH_LIFT);
    editedVersion.setTactileVisualMarks(TactileVisualAttributeType.PARTIALLY);
    editedVersion.setCreationDate(version1.getCreationDate());
    editedVersion.setEditionDate(version1.getEditionDate());
    editedVersion.setCreator(version1.getCreator());
    editedVersion.setEditor(version1.getEditor());
    editedVersion.setVersion(version1.getVersion());

    //when
    ReducedVariantException result = Assertions.assertThrows(
        ReducedVariantException.class,
        () -> relationService.updateVersion(version1, editedVersion));

    //then
    assertThat(result).isNotNull();
    ErrorResponse errorResponse = result.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(412);
    assertThat(errorResponse.getMessage()).isEqualTo("Object creation not allowed for reduced variant!");
    List<RelationVersion> relations = relationService.getRelationsByParentServicePointSloid(
        parentServicePointSloid);
    assertThat(relations).hasSize(1);
  }

  @Test
  void shouldUpdateRelationWhenWithMultipleReferencePoints() {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    StopPointVersion stopPointVersion = StopPointTestData.builderVersion1().meansOfTransport(Set.of(MeanOfTransport.TRAIN)).build();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointRepository.save(stopPointVersion);

    String referencePointSloid = "ch:1:sloid:7000:5";
    String elementSloid = "ch:1:sloid:7000:1";

    // First relation
    RelationVersion version1 = RelationTestData.builderVersion1().build();
    version1.setSloid(elementSloid);
    version1.setReferencePointSloid(referencePointSloid);
    version1.setParentServicePointSloid(parentServicePointSloid);
    RelationVersion savedVersion1 = relationRepository.saveAndFlush(version1);

    // Second relation to a different reference point
    RelationVersion version2 = RelationTestData.builderVersion1().build();
    version2.setSloid(elementSloid);
    version2.setReferencePointSloid("ch:1:sloid:7000:6");
    version2.setParentServicePointSloid(parentServicePointSloid);
    relationRepository.saveAndFlush(version2);

    // Edit first relation
    RelationVersion editedVersion = RelationTestData.builderVersion1().build();
    editedVersion.setSloid(savedVersion1.getSloid());
    editedVersion.setReferencePointSloid(referencePointSloid);
    editedVersion.setParentServicePointSloid(parentServicePointSloid);
    editedVersion.setValidFrom(savedVersion1.getValidFrom().plusDays(3));
    editedVersion.setContrastingAreas(StandardAttributeType.TO_BE_COMPLETED);
    editedVersion.setReferencePointElementType(ReferencePointElementType.PARKING_LOT);
    editedVersion.setStepFreeAccess(StepFreeAccessAttributeType.YES_WITH_LIFT);
    editedVersion.setTactileVisualMarks(TactileVisualAttributeType.PARTIALLY);
    editedVersion.setCreationDate(savedVersion1.getCreationDate());
    editedVersion.setEditionDate(savedVersion1.getEditionDate());
    editedVersion.setCreator(savedVersion1.getCreator());
    editedVersion.setEditor(savedVersion1.getEditor());
    editedVersion.setVersion(savedVersion1.getVersion());

    //when
    relationService.updateVersion(savedVersion1, editedVersion);
    List<RelationVersion> relationsByParentServicePointSloid = relationService.getRelationsByParentServicePointSloid(
        parentServicePointSloid);

    //then
    assertThat(relationsByParentServicePointSloid).isNotEmpty();
  }

}