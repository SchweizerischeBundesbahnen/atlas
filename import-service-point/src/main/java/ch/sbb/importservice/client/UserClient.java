package ch.sbb.importservice.client;

import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.api.user.administration.UserPermissionCreateModel;
import ch.sbb.importservice.config.OAuthFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "userClient", url = "${atlas.client.gateway.url}", configuration = OAuthFeignConfig.class)
public interface UserClient {

  @PostMapping(value = "/user-administration/v1/users")
  UserModel createUser(@RequestBody UserPermissionCreateModel permissionCreateModel);

  @PutMapping(value = "/user-administration/v1/users")
  UserModel updateUser(@RequestBody UserPermissionCreateModel permissionCreateModel);

  @GetMapping(value = "/user-administration/v1/users/{userId}")
  UserModel userAlreadyExists(@PathVariable String userId);

}
