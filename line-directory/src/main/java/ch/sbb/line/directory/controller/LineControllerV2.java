package ch.sbb.line.directory.controller;

import static java.util.stream.Collectors.toSet;

import ch.sbb.atlas.api.lidi.LineApiV2;
import ch.sbb.atlas.api.lidi.LineVersionModelV2;
import ch.sbb.line.directory.converter.CmykColorConverter;
import ch.sbb.line.directory.converter.RgbColorConverter;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.mapper.LineVersionWorkflowMapper;
import ch.sbb.line.directory.service.LineService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LineControllerV2 implements LineApiV2 {

  private final LineService lineService;

  @Override
  public List<LineVersionModelV2> getLineVersionsV2(String slnid) {
    return lineService.findLineVersions(slnid).stream().map(this::toModel).toList();
  }

  private LineVersionModelV2 toModel(LineVersion lineVersion) {
    return LineVersionModelV2.builder()
        .id(lineVersion.getId())
        .status(lineVersion.getStatus())
        .lineType(lineVersion.getLineType())
        .slnid(lineVersion.getSlnid())
        .number(lineVersion.getNumber())
        .longName(lineVersion.getLongName())
        .lineConcessionType(lineVersion.getConcessionType())
        .shortNumber(lineVersion.getShortNumber())
        .offerCategory(lineVersion.getOfferCategory())
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
}
