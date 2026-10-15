package ch.sbb.line.directory.module.line.controller;

import ch.sbb.atlas.api.lidi.CreateLineVersionModelV3;
import ch.sbb.atlas.api.lidi.LineApiV3;
import ch.sbb.atlas.api.lidi.LineVersionModelV3;
import ch.sbb.atlas.api.lidi.LineVersionV3RequestParams;
import ch.sbb.atlas.api.lidi.UpdateLineVersionModelV3;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.mapper.LineVersionV3Mapper;
import ch.sbb.line.directory.module.line.search.LineVersionV3SearchRestrictions;
import ch.sbb.line.directory.module.line.service.LineVersionV3Service;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LineControllerV3 implements LineApiV3 {

  private final LineVersionV3Service lineVersionV3Service;

  @Override
  public Container<LineVersionModelV3> getLineVersions(Pageable pageable, LineVersionV3RequestParams params) {
    Page<LineVersionModelV3> lineVersions = lineVersionV3Service.findAll(LineVersionV3SearchRestrictions.builder()
        .pageable(pageable)
        .lineVersionRequestParams(params)
        .build());
    return Container.<LineVersionModelV3>builder()
        .objects(lineVersions.getContent())
        .totalCount(lineVersions.getTotalElements())
        .build();
  }

  @Override
  public List<LineVersionModelV3> getLineVersionsV3(String slnid) {
    List<LineVersionModelV3> versions = lineVersionV3Service.findBySlnid(slnid);
    if (versions.isEmpty()) {
      throw new SlnidNotFoundException(slnid);
    }
    return versions;
  }

  @Override
  public LineVersionModelV3 createLineVersionV3(CreateLineVersionModelV3 newVersion) {
    LineVersionV3 createdVersion = lineVersionV3Service.create(LineVersionV3Mapper.toEntity(newVersion));
    return lineVersionV3Service.toModels(List.of(createdVersion)).getFirst();
  }

  @Override
  public List<LineVersionModelV3> updateLineVersionV3(Long id, UpdateLineVersionModelV3 newVersion) {
    LineVersionV3 versionToUpdate = lineVersionV3Service.getLineVersionById(id);
    List<LineVersionV3> updatedVersions = lineVersionV3Service.update(versionToUpdate,
        LineVersionV3Mapper.toEntityFromUpdate(newVersion, versionToUpdate),
        lineVersionV3Service.findLineVersions(versionToUpdate.getSlnid()));
    return lineVersionV3Service.toModels(updatedVersions);
  }

}
