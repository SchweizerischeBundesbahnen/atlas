package ch.sbb.atlas.api.user.administration;

import java.util.Set;

public interface UserAdministrationEvent {

  String getUserId();

  Set<PermissionModel> getPermissions();

}
