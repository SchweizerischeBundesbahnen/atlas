package ch.sbb.atlas.api.user.administration;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "User Information")
@RequestMapping("v1")
public interface UserInformationApiV1 {

  @GetMapping("search")
  @Operation(description = "Look up Users in SBB Azure AD via Graph API")
  List<UserModel> searchUsers(
          @RequestParam String searchQuery,
          @RequestParam(required = false) boolean searchInAtlas,
          @RequestParam(required = false)ApplicationType application);

}
