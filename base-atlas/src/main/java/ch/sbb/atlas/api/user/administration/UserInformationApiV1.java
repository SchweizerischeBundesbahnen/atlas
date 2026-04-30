package ch.sbb.atlas.api.user.administration;

import ch.sbb.atlas.annotation.AdminOnly;
import ch.sbb.atlas.annotation.AuthorizedOnly;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "User Information")
public interface UserInformationApiV1 {

  String SEARCH_IN_ATLAS = "/v1/search-in-atlas";
  String SEARCH_BO_DOSSIER_ANSWERING_USERS = "/v1/search-bo-dossier-answering-users";
  Set<String> ALLOWED_FOR_ATLAS_INTERNAL_ROLE = Set.of(SEARCH_IN_ATLAS, SEARCH_BO_DOSSIER_ANSWERING_USERS);

  @AdminOnly
  @GetMapping("/v1/search")
  @Operation(description = "Look up Users in SBB Azure AD via Graph API")
  List<UserModel> searchUsers(@RequestParam String searchQuery);

  @AuthorizedOnly
  @GetMapping(SEARCH_IN_ATLAS)
  @Operation(description = "Look up Users in SBB Atlas")
  List<UserModel> searchUsersInAtlas(
      @RequestParam String searchQuery,
      @RequestParam ApplicationType applicationType);

  @AuthorizedOnly
  @GetMapping(SEARCH_BO_DOSSIER_ANSWERING_USERS)
  @Operation(description = "Look up Users in atlas who may answer dossiers")
  List<UserModel> searchBoDossierAnsweringUsers(@RequestParam String searchQuery);
}
