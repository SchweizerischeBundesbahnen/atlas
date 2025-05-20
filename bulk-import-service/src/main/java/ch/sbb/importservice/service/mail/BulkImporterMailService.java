package ch.sbb.importservice.service.mail;

import ch.sbb.atlas.api.client.user.administration.UserAdministrationClient;
import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.importservice.exception.ImporterMailNotFoundException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BulkImporterMailService {

  private final UserAdministrationClient userAdministrationClient;

  public void checkImporterHasMail() {
    UserModel currentUser = userAdministrationClient.getCurrentUser();
    if (currentUser.getMail() == null) {
      throw new ImporterMailNotFoundException(currentUser);
    }
  }

  public String getMailOfCurrentUser() {
    UserModel currentUser = userAdministrationClient.getCurrentUser();
    return Objects.requireNonNull(currentUser.getMail(), "Mail of current user " + currentUser.getSbbUserId() + " is null");
  }
}
