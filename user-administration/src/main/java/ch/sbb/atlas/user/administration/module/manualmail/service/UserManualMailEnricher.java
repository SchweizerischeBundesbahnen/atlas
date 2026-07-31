package ch.sbb.atlas.user.administration.module.manualmail.service;

import ch.sbb.atlas.api.user.administration.UserModel;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserManualMailEnricher {

  private final UserManualMailService userManualMailService;

  public List<UserModel> enrich(List<UserModel> users) {

    if (users.isEmpty()) {
      return users;
    }

    Map<String, String> manualMailsByLowerCaseUserId = userManualMailService
        .getMailsByUserIds(users.stream().map(UserModel::getSbbUserId).toList())
        .entrySet().stream()
        .collect(Collectors.toMap(entry -> entry.getKey().toLowerCase(), Map.Entry::getValue));

    users.forEach(user -> user.setManualMail(manualMailsByLowerCaseUserId.get(user.getSbbUserId())));
    return users;
  }

  public UserModel enrich(UserModel user) {
    return enrich(List.of(user)).getFirst();
  }

}
