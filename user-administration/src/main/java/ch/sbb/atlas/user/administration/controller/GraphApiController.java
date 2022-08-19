package ch.sbb.atlas.user.administration.controller;

import ch.sbb.atlas.user.administration.api.GraphApiV1;
import ch.sbb.atlas.user.administration.service.GraphApiService;
import ch.sbb.atlas.user.administration.models.UserModel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GraphApiController implements GraphApiV1 {
  private final GraphApiService graphApiService;

  @Override
  public List<UserModel> searchUsers(String searchQuery) {
    return graphApiService.searchUsersByDisplayNameAndMail(searchQuery);
  }

  @Override
  public List<UserModel> resolveUsers(List<String> userIds) {
    return graphApiService.resolveLdapUserDataFromUserIds(userIds);
  }

}
