package ch.sbb.atlas.user.administration.security.model;

import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class UserModel {

  private String sbbUserId;

  private String lastName;

  private String firstName;

  private String mail;

  private String displayName;

  @Builder.Default
  private Set<UserPermissionModel> permissions = new HashSet<>();

}
