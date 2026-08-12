package ch.sbb.timetable.hearing.service;

import ch.sbb.atlas.api.client.user.administration.UserAdministrationClient;
import ch.sbb.atlas.api.model.ErrorResponse.Parameter;
import ch.sbb.atlas.api.user.administration.PermissionModel;
import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.model.exception.SimpleAtlasException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoContactPermissionService {

  private final UserAdministrationClient userAdministrationClient;

  public Optional<String> checkPermissionForBoContactMail(String mail) {
    if (mail != null) {
      UserModel user = userAdministrationClient.getUserByMail(mail);

      List<PermissionModel> permissions =
          user.getPermissions().stream()
              .filter(permissionModel -> permissionModel.getApplication().equals(ApplicationType.TIMETABLE_HEARING)).toList();

      boolean hasPermission = permissions.stream().map(PermissionModel::getPermissionRestrictions).flatMap(Collection::stream)
          .filter(i -> i.getType() == PermissionRestrictionType.TRANSPORT_COMPANY_DOSSIER_ANSWER)
          .anyMatch(i -> Boolean.parseBoolean(i.getValueAsString()));

      if (!hasPermission) {
        throw SimpleAtlasException.builder()
            .status(HttpStatus.PRECONDITION_FAILED)
            .messageAndError("You are not allowed to answer any questions")
            .displayCode("TTH.ERROR.NOT_ALLOWED_BO_CONTACT", List.of(new Parameter("mail", mail)))
            .build();
      }

      return Optional.of(user.getSbbUserId());
    }
    return Optional.empty();
  }
}
