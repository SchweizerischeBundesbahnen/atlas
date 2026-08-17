package ch.sbb.atlas.user.administration.module.manualmail.validation;

import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.user.administration.module.manualmail.exception.MailAlreadyInUseException;
import ch.sbb.atlas.user.administration.module.manualmail.service.UserManualMailOverrideService;
import ch.sbb.atlas.user.administration.module.userinformation.service.GraphApiService;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManualMailOverrideValidationService {

  private final UserManualMailOverrideService userManualMailOverrideService;
  private final GraphApiService graphApiService;

  public void validateMailNotInUse(String sbbUserId, String mail) {
    findManualMailOverrideOwner(mail)
        .or(() -> findAzureMailOwner(mail))
        .filter(mailOwner -> !mailOwner.equalsIgnoreCase(sbbUserId))
        .ifPresent(mailOwner -> {
          throw new MailAlreadyInUseException(mail, mailOwner);
        });
  }

  private Optional<String> findManualMailOverrideOwner(String mail) {
    return userManualMailOverrideService.findUserIdByMail(mail);
  }

  private Optional<String> findAzureMailOwner(String mail) {
    return graphApiService.searchUserByMail(mail).stream()
        .filter(user -> mail.equalsIgnoreCase(user.getMail()))
        .map(UserModel::getSbbUserId)
        .filter(Objects::nonNull)
        .findFirst();
  }

}
