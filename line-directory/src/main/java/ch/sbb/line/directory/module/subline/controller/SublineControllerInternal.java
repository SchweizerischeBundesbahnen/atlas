package ch.sbb.line.directory.module.subline.controller;

import ch.sbb.atlas.api.lidi.SublineApiInternal;
import ch.sbb.line.directory.module.subline.service.SublineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SublineControllerInternal implements SublineApiInternal {

  private final SublineService sublineService;

  @Override
  public void revokeSubline(String slnid) {
    sublineService.revokeSubline(slnid);
  }

  @Override
  public void deleteSublines(String slnid) {
    sublineService.deleteAll(slnid);
  }

}
