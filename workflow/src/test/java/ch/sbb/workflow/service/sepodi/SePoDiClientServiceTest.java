package ch.sbb.workflow.service.sepodi;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.model.Status;
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

  private StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder().sloid("ch:1:sloid:8000").versionId(1L).build();
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new SePoDiClientService(sePoDiClient);
  }

  @Test
  void shouldUpdateStatusInReview(){
    //given
    UpdateServicePointVersionModel updateServicePointVersionModel = UpdateServicePointVersionModel.builder().status(Status.IN_REVIEW).build();
    String sloid = "ch:1:sloid:8000";
    long versionId = 1L;
    doReturn(updateServicePointVersionModel).when(sePoDiClient).postServicePointsStatusUpdate(sloid, versionId,
        Status.IN_REVIEW);
    //when && then
    assertDoesNotThrow(() -> service.updateStoPointStatusToInReview(stopPointWorkflow));
  }

  @ParameterizedTest
  @EnumSource(value = Status.class, names = {"REVOKED","DRAFT", "WITHDRAWN", "VALIDATED"})
  void shouldNotUpdateStatusInReview(Status status){
    //given
    UpdateServicePointVersionModel updateServicePointVersionModel = UpdateServicePointVersionModel.builder().status(status).build();
    String sloid = "ch:1:sloid:8000";
    long versionId = 1L;
    doReturn(updateServicePointVersionModel).when(sePoDiClient).postServicePointsStatusUpdate(sloid, versionId, Status.IN_REVIEW);
    //when && then
    assertThrows(SePoDiClientWrongStatusReturnedException.class, () -> service.updateStoPointStatusToInReview(stopPointWorkflow));
  }

  @Test
  void shouldUpdateStatusInDraft(){
    //given
    UpdateServicePointVersionModel updateServicePointVersionModel =
        UpdateServicePointVersionModel.builder().status(Status.DRAFT).build();
    String sloid = "ch:1:sloid:8000";
    long versionId = 1L;
    doReturn(updateServicePointVersionModel).when(sePoDiClient).postServicePointsStatusUpdate(sloid, versionId,
        Status.DRAFT);
    //when && then
    assertDoesNotThrow(() -> service.updateStoPointStatusToInReview(stopPointWorkflow));
  }

  @ParameterizedTest
  @EnumSource(value = Status.class, names = {"REVOKED", "WITHDRAWN", "VALIDATED"})
  void shouldNotUpdateStatusToDraft(Status status){
    //given
    UpdateServicePointVersionModel updateServicePointVersionModel = UpdateServicePointVersionModel.builder().status(status).build();
    doReturn(updateServicePointVersionModel).when(sePoDiClient).postServicePointsStatusUpdate(stopPointWorkflow.getSloid(),
        stopPointWorkflow.getVersionId(),Status.DRAFT);
    //when && then
    assertThrows(SePoDiClientWrongStatusReturnedException.class, () -> service.updateStoPointStatusToDraft(stopPointWorkflow));
  }


}