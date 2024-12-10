package ch.sbb.line.directory.controller;

import ch.sbb.atlas.api.lidi.CreateSublineVersionModelV2;
import ch.sbb.atlas.api.lidi.ReadSublineVersionModelV2;
import ch.sbb.atlas.api.lidi.SublineApiV2;
import ch.sbb.atlas.api.lidi.SublineVersionModelV2;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
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
    if (versions.isEmpty()) {
      throw new SlnidNotFoundException(slnid);
    }
    String lineSlnid = versions.getFirst().getMainlineSlnid();
    LineVersion lineVersion = sublineService.getMainLineVersion(lineSlnid);
    return versions.stream().map(sublineVersion -> SublineMapper.toModel(sublineVersion, lineVersion)).toList();
  }

  @Override
  public ReadSublineVersionModelV2 createSublineVersionV2(CreateSublineVersionModelV2 newSublineVersion) {
    SublineVersion sublineVersion = SublineMapper.toEntity(newSublineVersion);
    sublineVersion.setStatus(Status.VALIDATED);
    SublineVersion createdVersion = sublineService.create(sublineVersion);

    LineVersion lineVersion = sublineService.getMainLineVersion(sublineVersion.getMainlineSlnid());
    return SublineMapper.toModel(createdVersion, lineVersion);
  }

  @Override
  public List<ReadSublineVersionModelV2> updateSublineVersionV2(Long id, SublineVersionModelV2 newVersion) {
    SublineVersion versionToUpdate = sublineService.findById(id)
        .orElseThrow(() -> new IdNotFoundException(id));
    sublineService.update(versionToUpdate, SublineMapper.toEntity(newVersion),
        sublineService.findSubline(versionToUpdate.getSlnid()));

    LineVersion lineVersion = sublineService.getMainLineVersion(versionToUpdate.getMainlineSlnid());
    return sublineService.findSubline(versionToUpdate.getSlnid()).stream().map(i -> SublineMapper.toModel(i, lineVersion))
        .toList();
  }


}
