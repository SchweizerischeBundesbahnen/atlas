package ch.sbb.atlas.useradministration.controller;

import ch.sbb.atlas.useradministration.api.GraphApiV1;
import ch.sbb.atlas.useradministration.service.GraphApiService;
import ch.sbb.atlas.useradministration.models.UserModel;
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
