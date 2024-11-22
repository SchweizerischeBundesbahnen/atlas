package ch.sbb.atlas.user.administration.controller;

import ch.sbb.atlas.api.user.administration.UserInformationApiV1;
import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.redact.Redacted;
import ch.sbb.atlas.user.administration.service.GraphApiService;
import ch.sbb.atlas.user.administration.service.UserAdministrationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserInformationApiController implements UserInformationApiV1 {

  private final GraphApiService graphApiService;
  private final UserAdministrationService administrationService;

  @Override
  public List<UserModel> searchUsers(String searchQuery) {
      return graphApiService.searchUsers(searchQuery);
  }

  @Override
  @Redacted
  public List<UserModel> searchUsersInAtlas(String searchQuery, ApplicationType applicationType) {
    List<UserModel> foundUsers = graphApiService.searchUsers(searchQuery);
    return administrationService.filterForPermittedUserInAtlas(foundUsers, applicationType);
  }
}
