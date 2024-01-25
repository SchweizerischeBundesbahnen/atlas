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
  void shouldNotCreateRelationPointWhenStopPointIsReduced() {
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
  void shouldNotUpdateRelationPointWhenStopPointIsReduced() {
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

}