package ch.sbb.atlas.user.administration.api;

import ch.sbb.atlas.base.service.model.api.Container;
import ch.sbb.atlas.user.administration.models.UserModel;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "User Administration")
@RequestMapping("/v1/users")
public interface UserAdministrationApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<UserModel> getUsers(@Parameter(hidden = true) Pageable pageable);

  @GetMapping("{userId}")
  UserModel getUser(@PathVariable String userId);

  @GetMapping("current")
  UserModel getCurrentUser();

}
