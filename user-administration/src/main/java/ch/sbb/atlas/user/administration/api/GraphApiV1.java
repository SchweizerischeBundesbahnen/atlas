package ch.sbb.atlas.user.administration.api;

import ch.sbb.atlas.user.administration.models.UserModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Atlas GraphApi")
@RequestMapping("v1")
public interface GraphApiV1 {

  @GetMapping("search")
  List<UserModel> searchUsers(@RequestParam String searchQuery);

  @GetMapping("resolve")
  List<UserModel> resolveUsers(@RequestParam List<String> userIds);

}
