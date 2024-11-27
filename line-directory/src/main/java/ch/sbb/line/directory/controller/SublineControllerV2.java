package ch.sbb.line.directory.controller;

import ch.sbb.atlas.api.lidi.ReadSublineVersionModelV2;
import ch.sbb.atlas.api.lidi.SublineApiV2;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.mapper.SublineMapper;
import ch.sbb.line.directory.service.SublineService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SublineControllerV2 implements SublineApiV2 {

  private final SublineService sublineService;

  @Override
  public List<ReadSublineVersionModelV2> getSublineVersionV2(String slnid) {
    List<SublineVersion> versions = sublineService.findSubline(slnid);
    String lineSlnid = versions.getFirst().getMainlineSlnid();
    LineVersion lineVersion = sublineService.getMainLineVersion(lineSlnid);
    return versions.stream().map(sublineVersion -> SublineMapper.toModel(sublineVersion, lineVersion)).toList();
  }


}
