package ch.sbb.line.directory.module.line.controller;

import ch.sbb.atlas.api.lidi.AffectedSublinesModel;
import ch.sbb.atlas.api.lidi.LineApiInternal;
import ch.sbb.atlas.api.lidi.LineModel;
import ch.sbb.atlas.api.lidi.LineRequestParams;
import ch.sbb.atlas.api.lidi.UpdateLineVersionModelV2;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.module.line.entity.Line;
import ch.sbb.line.directory.module.line.entity.LineVersion;
import ch.sbb.line.directory.module.line.mapper.LineMapper;
import ch.sbb.line.directory.module.line.search.LineSearchRestrictions;
import ch.sbb.line.directory.module.line.service.LineService;
import ch.sbb.line.directory.module.subline.service.SublineShorteningService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LineControllerInternal implements LineApiInternal {

  private final LineService lineService;
  private final SublineShorteningService sublineShorteningService;

  @Override
  public Container<LineModel> getOverview(Pageable pageable, LineRequestParams lineRequestParams) {
    log.info("Load Versions using pageable={}, params={}", pageable, lineRequestParams);
    Page<Line> lines = lineService.findAll(LineSearchRestrictions.builder()
        .pageable(pageable)
        .lineRequestParams(lineRequestParams)
        .build());
    List<LineModel> lineModels = lines.stream().map(LineMapper::toModel).toList();
    return Container.<LineModel>builder()
        .objects(lineModels)
        .totalCount(lines.getTotalElements()).build();
  }

  @Override
  public LineModel getLine(String slnid) {
    return lineService.findLine(slnid)
        .map(LineMapper::toModel)
        .orElseThrow(() -> new SlnidNotFoundException(slnid));
  }

  @Override
  public void revokeLine(String slnid) {
    List<LineVersion> lineVersions = lineService.revokeLine(slnid);
    if (lineVersions.isEmpty()) {
      throw new SlnidNotFoundException(slnid);
    }
  }

  @Override
  public void deleteLines(String slnid) {
    lineService.deleteAll(slnid);
  }

  @Override
  public AffectedSublinesModel checkAffectedSublines(Long id, UpdateLineVersionModelV2 newVersion) {
    LineVersion lineVersion = lineService.getLineVersionById(id);
    LineVersion editedVersion = LineMapper.toEntityFromUpdate(newVersion, lineVersion);
    return sublineShorteningService.checkAffectedSublines(lineVersion, editedVersion);
  }

}
