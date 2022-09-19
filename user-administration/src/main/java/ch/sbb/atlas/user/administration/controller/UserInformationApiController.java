package ch.sbb.atlas.user.administration.controller;

import ch.sbb.atlas.user.administration.api.UserInformationApiV1;
import ch.sbb.atlas.user.administration.service.GraphApiService;
import ch.sbb.atlas.user.administration.api.UserModel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserInformationApiController implements UserInformationApiV1 {
  private final GraphApiService graphApiService;

  @Override
  public List<UserModel> searchUsers(String searchQuery) {
    return graphApiService.searchUsers(searchQuery);
  }

}
