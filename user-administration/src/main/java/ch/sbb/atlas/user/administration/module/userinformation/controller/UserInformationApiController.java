package ch.sbb.atlas.user.administration.module.userinformation.controller;

import ch.sbb.atlas.api.user.administration.UserInformationApiV1;
import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.redact.Redacted;
import ch.sbb.atlas.user.administration.module.manualmail.service.UserManualMailEnricher;
import ch.sbb.atlas.user.administration.module.useradministration.service.UserAdministrationService;
import ch.sbb.atlas.user.administration.module.userinformation.service.UserSearchService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserInformationApiController implements UserInformationApiV1 {

  private final UserSearchService userSearchService;
  private final UserAdministrationService administrationService;
  private final UserManualMailEnricher userManualMailEnricher;

  @Override
  public List<UserModel> searchUsers(String searchQuery) {
    return userManualMailEnricher.enrich(userSearchService.searchUsers(searchQuery));
  }

  @Override
  @Redacted
  public List<UserModel> searchUsersInAtlas(String searchQuery, ApplicationType applicationType) {
    List<UserModel> foundUsers = userSearchService.searchUsers(searchQuery);
    return userManualMailEnricher.enrich(administrationService.filterForPermittedUserInAtlas(foundUsers, applicationType));
  }

  @Override
  @Redacted
  public List<UserModel> searchBoDossierAnsweringUsers(String searchQuery) {
    List<UserModel> foundUsers = userSearchService.searchUsers(searchQuery);
    return userManualMailEnricher.enrich(administrationService.filterForBoDossierAnsweringPermission(foundUsers));
  }
}
