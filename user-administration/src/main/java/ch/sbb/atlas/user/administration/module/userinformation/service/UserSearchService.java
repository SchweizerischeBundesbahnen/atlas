package ch.sbb.atlas.user.administration.module.userinformation.service;

import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.api.user.administration.enumeration.UserAccountStatus;
import ch.sbb.atlas.redact.Redacted;
import ch.sbb.atlas.user.administration.module.manualmail.service.UserManualMailOverrideService;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Searches users in Azure/Graph and additionally in the manual mail overrides, so that users are also found by a mail address
 * that only exists in atlas.
 */
@Service
@RequiredArgsConstructor
public class UserSearchService {

  private final GraphApiService graphApiService;
  private final UserManualMailOverrideService userManualMailOverrideService;

  @Redacted
  public List<UserModel> searchUsers(String searchQuery) {
    List<UserModel> foundUsers = graphApiService.searchUsers(searchQuery);

    Set<String> alreadyFoundUserIds = foundUsers.stream()
        .map(UserModel::getSbbUserId)
        .filter(Objects::nonNull)
        .map(sbbUserId -> sbbUserId.toLowerCase(Locale.ROOT))
        .collect(Collectors.toSet());

    List<String> userIdsFoundByManualMail = userManualMailOverrideService.findUserIdsByMailContaining(searchQuery).stream()
        .filter(sbbUserId -> !alreadyFoundUserIds.contains(sbbUserId.toLowerCase(Locale.ROOT)))
        .toList();

    if (userIdsFoundByManualMail.isEmpty()) {
      return foundUsers;
    }

    List<UserModel> result = new ArrayList<>(foundUsers);
    graphApiService.resolveUsers(userIdsFoundByManualMail).stream()
        .filter(user -> user.getAccountStatus() != UserAccountStatus.DELETED)
        .forEach(result::add);
    return result;
  }

}
