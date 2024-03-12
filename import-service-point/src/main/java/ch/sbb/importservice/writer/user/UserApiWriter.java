package ch.sbb.importservice.writer.user;

import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.api.user.administration.UserPermissionCreateModel;
import ch.sbb.atlas.imports.servicepoint.enumeration.ItemImportResponseStatus;
import ch.sbb.importservice.entity.user.DidokUserMapper;
import ch.sbb.importservice.entity.user.UserCsvModel;
import ch.sbb.importservice.writer.BaseApiWriter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@StepScope
public class UserApiWriter extends BaseApiWriter implements ItemWriter<UserCsvModel> {

  @Override
  public void write(Chunk<? extends UserCsvModel> userCsvModels) {
    List<UserCsvModel> models = new ArrayList<>(userCsvModels.getItems());
    List<UserPermissionCreateModel> userPermissionCreateModels = models.stream().map(DidokUserMapper::mapToUserPermissionCreateModel)
        .toList();
    Long stepExecutionId = stepExecution.getId();

    writeToApi(userPermissionCreateModels, stepExecutionId);
  }

  void writeToApi(List<UserPermissionCreateModel> userPermissionCreateModels, Long stepExecutionId) {
    userPermissionCreateModels.forEach(model -> {
      UserModel userAlreadyExists = userClient.userAlreadyExists(model.getSbbUserId());
      if (userAlreadyExists == null) {
        log.info("CREATE user: {} with permissions: {}", model.getSbbUserId(), model.getPermissions());
        UserModel user = userClient.createUser(model);
        saveItemProcessed(stepExecutionId, user.getUserId(), ItemImportResponseStatus.SUCCESS, "new user added");
      } else {
        log.info("UPDATE user: {} with permissions: {}", model.getSbbUserId(), model.getPermissions());
        UserModel user = userClient.updateUser(model);
        saveItemProcessed(stepExecutionId, user.getUserId(), ItemImportResponseStatus.SUCCESS, "user updated");
      }
    });
  }

}
