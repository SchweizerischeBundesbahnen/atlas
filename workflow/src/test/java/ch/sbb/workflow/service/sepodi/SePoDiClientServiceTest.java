package ch.sbb.workflow.service.sepodi;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateDesignationOfficialServicePointModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.workflow.client.SePoDiClient;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.exception.SePoDiClientWrongStatusReturnedException;
import ch.sbb.workflow.exception.StopPointWorkflowDesignationOfficialInvalidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SePoDiClientServiceTest {

  private SePoDiClientService service;

  @Mock
  private SePoDiClient sePoDiClient;

  private StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder().sloid("ch:1:sloid:8000")
          .versionId(1L)
          .id(1L)
          .designationOfficial("test")
          .build();

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new SePoDiClientService(sePoDiClient);
  }

  @Test
  void shouldUpdateStatusInReview() {
    //given
    String sloid = "ch:1:sloid:8000";
    long versionId = 1L;
    ReadServicePointVersionModel updateServicePointVersionModel = ReadServicePointVersionModel.builder()
        .sloid(sloid)
        .id(versionId)
        .status(Status.IN_REVIEW).build();
    doReturn(updateServicePointVersionModel).when(sePoDiClient).postServicePointsStatusUpdate(sloid, versionId, Status.IN_REVIEW);
    //when && then
    assertDoesNotThrow(
        () -> service.updateStopPointStatusToInReview(stopPointWorkflow.getSloid(), stopPointWorkflow.getVersionId()));
  }

  @ParameterizedTest
  @EnumSource(value = Status.class, names = {"REVOKED", "DRAFT", "WITHDRAWN", "VALIDATED"})
  void shouldNotUpdateStatusInReview(Status status) {
    //given
    ReadServicePointVersionModel updateServicePointVersionModel = ReadServicePointVersionModel.builder().status(status)
        .build();
    String sloid = "ch:1:sloid:8000";
    long versionId = 1L;
    doReturn(updateServicePointVersionModel).when(sePoDiClient).postServicePointsStatusUpdate(sloid, versionId, Status.IN_REVIEW);
    //when && then
    assertThrows(SePoDiClientWrongStatusReturnedException.class,
        () -> service.updateStopPointStatusToInReview(stopPointWorkflow.getSloid(), stopPointWorkflow.getVersionId()));
  }

  @Test
  void shouldUpdateStatusInDraft() {
    //given
    ReadServicePointVersionModel updateServicePointVersionModel =
        ReadServicePointVersionModel.builder().status(Status.DRAFT).build();
    String sloid = "ch:1:sloid:8000";
    long versionId = 1L;
    doReturn(updateServicePointVersionModel).when(sePoDiClient).postServicePointsStatusUpdate(sloid, versionId,
        Status.DRAFT);
    //when && then
    assertDoesNotThrow(
        () -> service.updateStopPointStatusToInReview(stopPointWorkflow.getSloid(), stopPointWorkflow.getVersionId()));
  }

  @ParameterizedTest
  @EnumSource(value = Status.class, names = {"REVOKED", "WITHDRAWN", "VALIDATED"})
  void shouldNotUpdateStatusToDraft(Status status) {
    //given
    ReadServicePointVersionModel updateServicePointVersionModel = ReadServicePointVersionModel.builder().status(status)
        .build();
    doReturn(updateServicePointVersionModel).when(sePoDiClient).postServicePointsStatusUpdate(stopPointWorkflow.getSloid(),
        stopPointWorkflow.getVersionId(), Status.DRAFT);
    //when && then
    assertThrows(SePoDiClientWrongStatusReturnedException.class, () -> service.updateStopPointStatusToDraft(stopPointWorkflow));
  }

  @Test
  void shouldUpdateStatusToValidated() {
    //given
    ReadServicePointVersionModel updateServicePointVersionModel = ReadServicePointVersionModel.builder().status(Status.VALIDATED)
        .build();
    doReturn(updateServicePointVersionModel).when(sePoDiClient)
        .postServicePointsStatusUpdate("ch:1:sloid:8000", 1L, Status.VALIDATED);

    //when && then
    assertDoesNotThrow(
        () -> service.updateStopPointStatusToInReview(stopPointWorkflow.getSloid(), stopPointWorkflow.getVersionId()));
  }

  @Test
  void shouldNotUpdateStatusToValidated() {
    //given
    ReadServicePointVersionModel updateServicePointVersionModel = ReadServicePointVersionModel.builder().status(Status.REVOKED)
        .build();
    doReturn(updateServicePointVersionModel).when(sePoDiClient).postServicePointsStatusUpdate(stopPointWorkflow.getSloid(),
        stopPointWorkflow.getVersionId(), Status.VALIDATED);
    //when && then
    assertThrows(SePoDiClientWrongStatusReturnedException.class,
        () -> service.updateStoPointStatusToValidated(stopPointWorkflow));
  }

  @Test
  void shouldUpdateDesignationOfficial() {
    //given
    String sloid = "ch:1:sloid:8000";
    long versionId = 1L;

    UpdateDesignationOfficialServicePointModel updateDesignationOfficialServicePointModel = UpdateDesignationOfficialServicePointModel.builder()
            .designationOfficial("test")
            .build();

    ReadServicePointVersionModel updateServicePointVersionModel = ReadServicePointVersionModel.builder()
            .sloid(sloid)
            .id(versionId)
            .status(Status.IN_REVIEW)
            .designationOfficial("Designerica")
            .build();
    doReturn(updateServicePointVersionModel).when(sePoDiClient).updateServicePointDesignationOfficial(versionId, updateDesignationOfficialServicePointModel);

    //when && then
    assertDoesNotThrow(
            () -> service.updateDesignationOfficialServicePoint(stopPointWorkflow));
  }

  @Test
  void shouldNotUpdateDesignationOfficial() {
    //given
    String sloid = "ch:1:sloid:8000";
    long versionId = 1L;

    UpdateDesignationOfficialServicePointModel updateDesignationOfficialServicePointModel = UpdateDesignationOfficialServicePointModel.builder()
            .designationOfficial("test")
            .build();

    ReadServicePointVersionModel updateServicePointVersionModel = ReadServicePointVersionModel.builder()
            .sloid(sloid)
            .id(versionId)
            .status(Status.IN_REVIEW)
            .designationOfficial("Designerica")
            .build();
    doReturn(updateServicePointVersionModel).when(sePoDiClient).updateServicePointDesignationOfficial(versionId, updateDesignationOfficialServicePointModel);

    //when && then
    assertThrows(StopPointWorkflowDesignationOfficialInvalidException.class, () -> service.updateDesignationOfficialServicePoint(stopPointWorkflow));

  }
}