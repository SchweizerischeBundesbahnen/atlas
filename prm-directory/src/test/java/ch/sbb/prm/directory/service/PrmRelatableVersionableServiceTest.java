package ch.sbb.prm.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.prm.enumeration.RecordingStatus;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.enumeration.TactileVisualAttributeType;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.BasePrmEntityVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.RelationVersion.RelationVersionBuilder;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PrmRelatableVersionableServiceTest {

  private static final String ELEMENT_SLOID = "elementSloid";

  @Mock
  private VersionableService versionableService;
  @Mock
  private StopPointService stopPointService;
  @Mock
  private RelationService relationService;
  @Mock
  private ReferencePointRepository referencePointRepository;
  @Mock
  private PrmLocationService locationService;

  private RelatableService relatableService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    relatableService = new RelatableService(versionableService, stopPointService, relationService, referencePointRepository,
        locationService);
  }

  @Test
  void shouldEvaluateRecordingStatusWithRelationsAndElementStatusToComplete() {
    when(relationService.getRelationsBySloid(any())).thenReturn(List.of(getRelation().build()));

    RecordingStatus result = relatableService.getRecordingStatusIncludingRelation(ELEMENT_SLOID, RecordingStatus.COMPLETE);
    assertThat(result).isEqualTo(RecordingStatus.COMPLETE);
  }

  @Test
  void shouldEvaluateRecordingStatusWithRelationsAndElementStatusToIncomplete() {
    when(relationService.getRelationsBySloid(any())).thenReturn(List.of(getRelation().build()));

    RecordingStatus result = relatableService.getRecordingStatusIncludingRelation(ELEMENT_SLOID, RecordingStatus.INCOMPLETE);
    assertThat(result).isEqualTo(RecordingStatus.INCOMPLETE);
  }

  @Test
  void shouldEvaluateRecordingStatusWithIncompleteRelationsAndElementStatusToIncomplete() {
    when(relationService.getRelationsBySloid(any())).thenReturn(List.of(getRelation().tactileVisualMarks(TactileVisualAttributeType.TO_BE_COMPLETED).build()));

    RecordingStatus result = relatableService.getRecordingStatusIncludingRelation(ELEMENT_SLOID, RecordingStatus.COMPLETE);
    assertThat(result).isEqualTo(RecordingStatus.INCOMPLETE);
  }

  @Test
  void shouldEvaluateRecordingStatusWithoutRelationsToComplete() {
    when(relationService.getRelationsBySloid(any())).thenReturn(Collections.emptyList());

    RecordingStatus result = relatableService.getRecordingStatusIncludingRelation(ELEMENT_SLOID, RecordingStatus.COMPLETE);
    assertThat(result).isEqualTo(RecordingStatus.COMPLETE);
  }

  @Test
  void shouldEvaluateRecordingStatusWithoutRelationsToIncomplete() {
    when(relationService.getRelationsBySloid(any())).thenReturn(Collections.emptyList());

    RecordingStatus result = relatableService.getRecordingStatusIncludingRelation(ELEMENT_SLOID, RecordingStatus.INCOMPLETE);
    assertThat(result).isEqualTo(RecordingStatus.INCOMPLETE);
  }

  private static RelationVersionBuilder<?, ?> getRelation() {
    return RelationVersion.builder()
        .sloid("sloid")
        .referencePointSloid("ch:1:sloid:123456")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.now().minusDays(30))
        .validTo(LocalDate.now().plusDays(30))
        .parentServicePointSloid("parentServicePointSloid")
        .referencePointElementType(ReferencePointElementType.TOILET)
        .contrastingAreas(StandardAttributeType.YES)
        .tactileVisualMarks(TactileVisualAttributeType.YES)
        .stepFreeAccess(StepFreeAccessAttributeType.NO);
  }

  private static class RelatableService extends PrmRelatableVersionableService<DummyVersion> {

    protected RelatableService(VersionableService versionableService,
        StopPointService stopPointService, RelationService relationService,
        ReferencePointRepository referencePointRepository, PrmLocationService locationService) {
      super(versionableService, stopPointService, relationService, referencePointRepository, locationService);
    }

    @Override
    protected ReferencePointElementType getReferencePointElementType() {
      throw new UnsupportedOperationException();
    }

    @Override
    protected SloidType getSloidType() {
      throw new UnsupportedOperationException();
    }

    @Override
    protected void incrementVersion(String sloid) {
      throw new UnsupportedOperationException();
    }

    @Override
    protected DummyVersion save(DummyVersion version) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<DummyVersion> getAllVersions(String sloid) {
      throw new UnsupportedOperationException();
    }

    @Override
    protected void applyVersioning(List<VersionedObject> versionedObjects) {
      throw new UnsupportedOperationException();
    }
  }

  @EqualsAndHashCode(callSuper = true)
  @Data
  private static class DummyVersion extends BasePrmEntityVersion implements Relatable, PrmVersionable {

    private Long id;

    private String designation;

    private String additionalInformation;

  }

}