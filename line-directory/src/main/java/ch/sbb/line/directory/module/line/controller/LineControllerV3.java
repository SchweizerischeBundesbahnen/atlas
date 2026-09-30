package ch.sbb.line.directory.module.line.controller;

import ch.sbb.atlas.api.lidi.LineApiV3;
import ch.sbb.atlas.api.lidi.LineVersionModelV3;
import ch.sbb.atlas.api.lidi.LineVersionV3RequestParams;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
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

}
