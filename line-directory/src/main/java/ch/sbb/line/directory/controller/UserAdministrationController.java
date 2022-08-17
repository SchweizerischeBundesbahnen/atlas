package ch.sbb.line.directory.controller;

import ch.sbb.atlas.model.api.Container;
import ch.sbb.line.directory.api.UserAdministrationApiV1;
import ch.sbb.line.directory.entity.UserPermission;
import ch.sbb.line.directory.service.UserAdministrationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserAdministrationController implements UserAdministrationApiV1 {

  private final UserAdministrationService userAdministrationService;

  @Override
  public Container<String> getUsers(Pageable pageable) {
    Page<String> userPage = userAdministrationService.getUserPage(pageable);
    return Container.<String>builder()
                    .totalCount(userPage.getTotalElements())
                    .objects(userPage.getContent())
                    .build();
  }

  @Override
  public List<UserPermission> getUserPermissions(String userId){
    return userAdministrationService.getUserPermissions(userId);
  }

}
