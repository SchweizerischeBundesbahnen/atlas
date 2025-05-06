package ch.sbb.line.directory.controller;

import static java.util.stream.Collectors.toSet;

import ch.sbb.atlas.api.lidi.LineApiV1;
import ch.sbb.atlas.api.lidi.LineModel;
import ch.sbb.atlas.api.lidi.LineRequestParams;
import ch.sbb.atlas.api.lidi.LineVersionModel;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.line.directory.converter.CmykColorConverter;
import ch.sbb.line.directory.converter.RgbColorConverter;
import ch.sbb.line.directory.entity.Line;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.mapper.LineVersionWorkflowMapper;
import ch.sbb.line.directory.model.search.LineSearchRestrictions;
import ch.sbb.line.directory.service.LineService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LineControllerV1 implements LineApiV1 {

  private final LineService lineService;

  @Override
  public Container<LineModel> getLines(Pageable pageable, LineRequestParams lineRequestParams) {
    log.info("Load Versions using pageable={}, params={}", pageable, lineRequestParams);
    Page<Line> lines = lineService.findAll(LineSearchRestrictions.builder()
        .pageable(pageable)
        .lineRequestParams(lineRequestParams)
        .build());
    List<LineModel> lineModels = lines.stream().map(this::toModel).toList();
    return Container.<LineModel>builder()
        .objects(lineModels)
        .totalCount(lines.getTotalElements()).build();
  }

  @Override
  public LineModel getLine(String slnid) {
    return lineService.findLine(slnid)
        .map(this::toModel)
        .orElseThrow(() -> new SlnidNotFoundException(slnid));
  }

  @Override
  public List<LineVersionModel> getLineVersions(String slnid) {
    List<LineVersionModel> lineVersionModels = lineService.findLineVersionsForV1(slnid).stream()
        .map(this::toModel)
        .toList();
    if (lineVersionModels.isEmpty()) {
      throw new SlnidNotFoundException(slnid);
    }
    return lineVersionModels;
  }

  private LineVersionModel toModel(LineVersion lineVersion) {
    return LineVersionModel.builder()
        .id(lineVersion.getId())
        .status(lineVersion.getStatus())
        .lineType(lineVersion.getLineType())
        .slnid(lineVersion.getSlnid())
        .paymentType(lineVersion.getPaymentType())
        .number(lineVersion.getNumber())
        .alternativeName(lineVersion.getAlternativeName())
        .combinationName(lineVersion.getCombinationName())
        .longName(lineVersion.getLongName())
        .colorFontRgb(RgbColorConverter.toHex(lineVersion.getColorFontRgb()))
        .colorBackRgb(RgbColorConverter.toHex(lineVersion.getColorBackRgb()))
        .colorFontCmyk(CmykColorConverter.toCmykString(
            lineVersion.getColorFontCmyk()))
        .colorBackCmyk(
            CmykColorConverter.toCmykString(lineVersion.getColorBackCmyk()))
        .description(lineVersion.getDescription())
        .icon(lineVersion.getIcon())
        .validFrom(lineVersion.getValidFrom())
        .validTo(lineVersion.getValidTo())
        .businessOrganisation(lineVersion.getBusinessOrganisation())
        .comment(lineVersion.getComment())
        .swissLineNumber(lineVersion.getSwissLineNumber())
        .etagVersion(lineVersion.getVersion())
        .lineVersionWorkflows(
            lineVersion.getLineVersionWorkflows()
                .stream()
                .map(LineVersionWorkflowMapper::toModel).collect(toSet()))
        .creator(lineVersion.getCreator())
        .creationDate(lineVersion.getCreationDate())
        .editor(lineVersion.getEditor())
        .editionDate(lineVersion.getEditionDate())
        .build();
  }

  private LineModel toModel(Line line) {
    return LineModel.builder()
        .status(line.getStatus())
        .lidiElementType(line.getLidiElementType())
        .elementType(line.getElementType())
        .slnid(line.getSlnid())
        .number(line.getNumber())
        .description(line.getDescription())
        .validFrom(line.getValidFrom())
        .validTo(line.getValidTo())
        .businessOrganisation(line.getBusinessOrganisation())
        .swissLineNumber(line.getSwissLineNumber())
        .build();
  }

}
