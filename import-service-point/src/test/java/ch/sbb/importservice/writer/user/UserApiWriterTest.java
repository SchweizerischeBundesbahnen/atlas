package ch.sbb.importservice.writer.user;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.api.user.administration.UserPermissionCreateModel;
import ch.sbb.importservice.client.UserClient;
import ch.sbb.importservice.repository.ImportProcessedItemRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;

@ExtendWith(MockitoExtension.class)
class UserApiWriterTest {

  @Mock
  private StepExecution stepExecution;
  @Mock
  private UserClient userClient;

  @Mock
  private ImportProcessedItemRepository importProcessedItemRepository;

  @InjectMocks
  private UserApiWriter userApiWriter;

  @Test
  void shouldWriteToApiWhenUserExists() {
    //given
    List<UserPermissionCreateModel> userPermissionCreateModels = new ArrayList<>();
    UserPermissionCreateModel model = UserPermissionCreateModel.builder()
        .permissions(List.of())
        .sbbUserId("e123456")
        .build();
    userPermissionCreateModels.add(model);
    UserModel e123456 = UserModel.builder().sbbUserId("e123456").permissions(Set.of()).build();

    when(userClient.userAlreadyExists("e123456")).thenReturn(e123456);
    when(userClient.updateUser(model)).thenReturn(e123456);
    JobExecution jobExecution = new JobExecution(132L);
    jobExecution.setJobInstance(new JobInstance(123L, "MyJob"));
    when(stepExecution.getJobExecution()).thenReturn(jobExecution);
    //when
    userApiWriter.writeToApi(userPermissionCreateModels,123L);
    //then
    verify(userClient).updateUser(model);
  }

  @Test
  void shouldWriteToApiWhenUserDoesNotExists() {
    //given
    List<UserPermissionCreateModel> userPermissionCreateModels = new ArrayList<>();
    UserPermissionCreateModel model = UserPermissionCreateModel.builder()
        .permissions(List.of())
        .sbbUserId("e123456")
        .build();
    userPermissionCreateModels.add(model);
    UserModel e123456 = UserModel.builder().sbbUserId("e123456").permissions(Set.of()).build();

    when(userClient.userAlreadyExists("e123456")).thenReturn(null);
    when(userClient.createUser(model)).thenReturn(e123456);
    JobExecution jobExecution = new JobExecution(132L);
    jobExecution.setJobInstance(new JobInstance(123L, "MyJob"));
    when(stepExecution.getJobExecution()).thenReturn(jobExecution);
    //when
    userApiWriter.writeToApi(userPermissionCreateModels,123L);
    //then
    verify(userClient).createUser(model);
  }

}