package ch.sbb.workflow.service.sepodi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateDesignationOfficialServicePointModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.workflow.client.SePoDiAdminClient;
import ch.sbb.workflow.client.SePoDiClient;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.exception.SePoDiClientWrongStatusReturnedException;
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

  @Mock
  private SePoDiAdminClient sePoDiAdminClient;

  private final StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder().sloid("ch:1:sloid:8000")
          .versionId(1L)
          .id(1L)
          .designationOfficial("test")
          .build();


  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new SePoDiClientService(sePoDiClient, sePoDiAdminClient);
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
        () -> service.updateStopPointStatusToDraft(stopPointWorkflow));
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
    doReturn(updateServicePointVersionModel).when(sePoDiAdminClient)
        .postServicePointsStatusUpdate("ch:1:sloid:8000", 1L, Status.VALIDATED);

    //when && then
    assertDoesNotThrow(
        () -> service.updateStopPointStatusToValidatedAsAdmin(stopPointWorkflow));
    verify(sePoDiAdminClient).postServicePointsStatusUpdate(any(), any(), eq(Status.VALIDATED));
  }

  @Test
  void shouldNotUpdateStatusToValidated() {
    //given
    ReadServicePointVersionModel updateServicePointVersionModel = ReadServicePointVersionModel.builder().status(Status.REVOKED)
        .build();
    doReturn(updateServicePointVersionModel).when(sePoDiAdminClient).postServicePointsStatusUpdate(stopPointWorkflow.getSloid(),
        stopPointWorkflow.getVersionId(), Status.VALIDATED);
    //when && then
    assertThrows(SePoDiClientWrongStatusReturnedException.class,
        () -> service.updateStopPointStatusToValidatedAsAdmin(stopPointWorkflow));
    verify(sePoDiAdminClient).postServicePointsStatusUpdate(any(), any(), eq(Status.VALIDATED));
  }

  @Test
  void shouldUpdateDesignationOfficial() {
    //given
    String sloid = "ch:1:sloid:8000";
    long versionId = 1L;

    UpdateDesignationOfficialServicePointModel updateDesignationOfficialServicePointModel =
        UpdateDesignationOfficialServicePointModel.builder()
            .designationOfficial("test")
            .build();

    ReadServicePointVersionModel updateServicePointVersionModel = ReadServicePointVersionModel.builder()
        .sloid(sloid)
        .id(versionId)
        .status(Status.IN_REVIEW)
        .designationOfficial("Designerica")
        .build();
    doReturn(updateServicePointVersionModel).when(sePoDiClient)
        .updateServicePointDesignationOfficial(versionId, updateDesignationOfficialServicePointModel);

    //when && then
    assertDoesNotThrow(
        () -> service.updateDesignationOfficialServicePoint(stopPointWorkflow));
  }

  @Test
  void shouldReturnVersionModelByUpdateStopPointStatusToValidatedAsAdminForJob() {
    //given
    ReadServicePointVersionModel updateServicePointVersionModel = ReadServicePointVersionModel.builder().status(Status.VALIDATED)
        .build();
    doReturn(updateServicePointVersionModel).when(sePoDiAdminClient)
        .postServicePointsStatusUpdate("ch:1:sloid:8000", 1L, Status.VALIDATED);

    //when
    ReadServicePointVersionModel result = service.updateStopPointStatusToValidatedAsAdminForJob(
        stopPointWorkflow);
    //then
    assertThat(result).isNotNull();
  }

  @Test
  void shouldNotReturnVersionModelByUpdateStopPointStatusToValidatedAsAdminForJob() {
    //given
    ReadServicePointVersionModel updateServicePointVersionModel = ReadServicePointVersionModel.builder().status(Status.IN_REVIEW)
        .build();
    doReturn(updateServicePointVersionModel).when(sePoDiAdminClient)
        .postServicePointsStatusUpdate(stopPointWorkflow.getSloid(), stopPointWorkflow.getId(), Status.VALIDATED);

    //when
    ReadServicePointVersionModel result = service.updateStopPointStatusToValidatedAsAdminForJob(
        stopPointWorkflow);
    //then
    assertThat(result).isNull();
  }

  @Test
  void shouldReturnServicePointVersionModelWhenGetServicePointById() {
    //when
    ReadServicePointVersionModel result = service.getServicePointById(
        stopPointWorkflow.getId());
    //then
    assertThat(result).isNull();
  }

  @Test
  void shouldVerifyNotFoundExceptionIsThrownWhenGetServicePointById() {
    // given
    AtlasException atlasException = new IdNotFoundException(stopPointWorkflow.getId());
    doThrow(atlasException).when(sePoDiClient).getServicePointById(stopPointWorkflow.getId());
    // when & then
    assertThatThrownBy(() -> service.getServicePointById(stopPointWorkflow.getId()))
        .isInstanceOf(IdNotFoundException.class)
        .hasMessageContaining("Entity not found");
  }

}