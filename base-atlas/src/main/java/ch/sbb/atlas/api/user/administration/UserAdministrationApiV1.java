package ch.sbb.atlas.api.user.administration;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.user.administration.enumeration.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "User Administration")
@RequestMapping("/v1/users")
public interface UserAdministrationApiV1 {

  @GetMapping
  @PageableAsQueryParam
  @Operation(description = "Retrieve Overview for all the managed Users")
  Container<UserModel> getUsers(@Parameter(hidden = true) Pageable pageable,
      @RequestParam(required = false) Set<String> permissionRestrictions,
      @RequestParam(required = false) PermissionRestrictionType type,
      @RequestParam(required = false) Set<ApplicationType> applicationTypes);

  @GetMapping("{userId}")
  @Operation(description = "Retrieve User Information for a given user")
  UserModel getUser(@PathVariable String userId);

  @GetMapping("{userId}/displayname")
  @Operation(description = "Retrieve Users DisplayName for a given user")
  UserDisplayNameModel getUserDisplayName(@PathVariable String userId);

  @GetMapping("current")
  UserModel getCurrentUser();

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(description = "Register a user with given permissions")
  UserModel createUserPermission(@RequestBody @Valid UserPermissionCreateModel user);

  @PutMapping
  @Operation(description = "Update the user permissions of a user")
  UserModel updateUserPermissions(@RequestBody @Valid UserPermissionCreateModel editedPermissions);

}
