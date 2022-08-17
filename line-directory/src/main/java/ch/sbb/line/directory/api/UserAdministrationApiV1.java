package ch.sbb.line.directory.api;

import ch.sbb.atlas.model.api.Container;
import ch.sbb.line.directory.entity.UserPermission;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "LiDi User Administration")
@RequestMapping("/v1/users")
public interface UserAdministrationApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<String> getUsers(@Parameter(hidden = true) Pageable pageable);

  @GetMapping("{userId}/permissions")
  List<UserPermission> getUserPermissions(@PathVariable String userId);

}
