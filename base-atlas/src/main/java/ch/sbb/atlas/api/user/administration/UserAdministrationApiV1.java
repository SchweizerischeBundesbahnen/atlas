package ch.sbb.atlas.api.user.administration;

import ch.sbb.atlas.annotation.AdminOnly;
import ch.sbb.atlas.annotation.AuthorizedOnly;
import ch.sbb.atlas.annotation.UnauthorizedAllowed;
import ch.sbb.atlas.annotation.UnauthorizedAllowed.FurtherLimitations;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Set;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "User Administration")
public interface UserAdministrationApiV1 {

  String BASE_PATH = "v1/users";

  @AuthorizedOnly
  @GetMapping(BASE_PATH)
  @PageableAsQueryParam
  @Operation(description = "Retrieve Overview for all the managed Users")
  Container<UserModel> getUsers(@Parameter(hidden = true) Pageable pageable,
      @RequestParam(required = false) Set<String> permissionRestrictions,
      @RequestParam(required = false) PermissionRestrictionType type,
      @RequestParam(required = false) Set<ApplicationType> applicationTypes);

  @AuthorizedOnly
  @GetMapping(BASE_PATH + "/{userId}")
  @Operation(description = "Retrieve User Information for a given user")
  UserModel getUser(@PathVariable String userId);

  @AuthorizedOnly
  @GetMapping(BASE_PATH + "/mail")
  @Operation(description = "Retrieve User Information for a given user by mail")
  UserModel getUserByMail(@RequestParam String mail);

  @UnauthorizedAllowed(limitations = FurtherLimitations.REDACTED)
  @GetMapping(BASE_PATH + "/{userId}/displayname")
  @Operation(description = "Retrieve Users DisplayName for a given user")
  UserDisplayNameModel getUserDisplayName(@PathVariable String userId);

  @UnauthorizedAllowed(limitations = FurtherLimitations.REDACTED)
  @GetMapping(BASE_PATH + "/current")
  UserModel getCurrentUser();

  @AdminOnly
  @PostMapping(BASE_PATH)
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(description = "Register a user")
  UserModel createUserPermission(@RequestBody @Valid UserPermissionCreateModel user);

  @AdminOnly
  @PutMapping(BASE_PATH + "/{userId}/{application}")
  @Operation(description = "Update the permissions of a user")
  UserModel updateUserPermissions(
      @PathVariable String userId,
      @PathVariable ApplicationType application,
      @RequestBody @Valid PermissionModel editedPermissions);

  @AdminOnly
  @PostMapping(BASE_PATH + "/sync-permissions")
  @Operation(description = "Write all user permission to kafka again for redistribution")
  void syncPermissions();

}
