package ch.sbb.line.directory.controller;

import static java.util.stream.Collectors.toList;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.line.directory.api.Container;
import ch.sbb.line.directory.api.CoverageModel;
import ch.sbb.line.directory.api.LineApiV1;
import ch.sbb.line.directory.api.LineModel;
import ch.sbb.line.directory.api.LineVersionModel;
import ch.sbb.line.directory.converter.CmykColorConverter;
import ch.sbb.line.directory.converter.RgbColorConverter;
import ch.sbb.line.directory.entity.Line;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.exception.LiDiNotFoundException.SlnidNotFoundException;
import ch.sbb.line.directory.model.LineSearchRestrictions;
import ch.sbb.line.directory.service.CoverageService;
import ch.sbb.line.directory.service.LineService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LineController implements LineApiV1 {

  private final LineService lineService;
  private final CoverageService coverageService;

  @Override
  public Container<LineModel> getLines(Pageable pageable, Optional<String> swissLineNumber,
      List<String> searchCriteria, List<Status> statusRestrictions, List<LineType> typeRestrictions,
      Optional<LocalDate> validOn) {
    log.info("Load Versions using pageable={}", pageable);
    Page<Line> lines = lineService.findAll(LineSearchRestrictions.builder()
                                                                 .pageable(pageable)
                                                                 .searchCriterias(searchCriteria)
                                                                 .statusRestrictions(
                                                                     statusRestrictions)
                                                                 .validOn(validOn)
                                                                 .typeRestrictions(typeRestrictions)
                                                                 .swissLineNumber(swissLineNumber)
                                                                 .build());
    List<LineModel> lineModels = lines.stream().map(this::toModel).collect(toList());
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
  public List<LineModel> getCoveredLines() {
    return lineService.getAllCoveredLines().stream().map(this::toModel).collect(toList());
  }

  @Override
  public List<LineVersionModel> getCoveredVersionLines() {
    return lineService.getAllCoveredLineVersions().stream().map(this::toModel).collect(toList());
  }

  @Override
  public List<LineVersionModel> getLineVersions(String slnid) {
    List<LineVersionModel> lineVersionModels = lineService.findLineVersions(slnid).stream()
                                                          .map(this::toModel)
                                                          .collect(toList());
    if (lineVersionModels.isEmpty()) {
      throw new SlnidNotFoundException(slnid);
    }
    return lineVersionModels;
  }

  private LineModel toModel(Line lineVersion) {
    return LineModel.builder()
                    .status(lineVersion.getStatus())
                    .lineType(lineVersion.getLineType())
                    .slnid(lineVersion.getSlnid())
                    .number(lineVersion.getNumber())
                    .description(lineVersion.getDescription())
                    .validFrom(lineVersion.getValidFrom())
                    .validTo(lineVersion.getValidTo())
                    .businessOrganisation(lineVersion.getBusinessOrganisation())
                    .swissLineNumber(lineVersion.getSwissLineNumber())
                    .build();
  }

  @Override
  public LineVersionModel createLineVersion(LineVersionModel newVersion) {
    LineVersion newLineVersion = toEntity(newVersion);
    newLineVersion.setStatus(Status.ACTIVE);
    LineVersion createdVersion = lineService.save(newLineVersion);
    return toModel(createdVersion);
  }

  @Override
  public List<LineVersionModel> updateLineVersion(Long id, LineVersionModel newVersion) {
    LineVersion versionToUpdate = lineService.findById(id)
                                             .orElseThrow(() -> new IdNotFoundException(id));
    lineService.updateVersion(versionToUpdate, toEntity(newVersion));
    return lineService.findLineVersions(versionToUpdate.getSlnid()).stream().map(this::toModel)
                      .collect(toList());
  }

  @Override
  public CoverageModel getLineCoverage(String slnid) {
    return CoverageModel.toModel(coverageService.getSublineCoverageBySlnidAndLineModelType(slnid));
  }

  @Override
  public void deleteLines(String slnid) {
    lineService.deleteAll(slnid);
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
                           .build();
  }

  private LineVersion toEntity(LineVersionModel lineVersionModel) {
    return LineVersion.builder()
                      .id(lineVersionModel.getId())
                      .lineType(lineVersionModel.getLineType())
                      .slnid(lineVersionModel.getSlnid())
                      .paymentType(lineVersionModel.getPaymentType())
                      .number(lineVersionModel.getNumber())
                      .alternativeName(lineVersionModel.getAlternativeName())
                      .combinationName(lineVersionModel.getCombinationName())
                      .longName(lineVersionModel.getLongName())
                      .colorFontRgb(RgbColorConverter.fromHex(lineVersionModel.getColorFontRgb()))
                      .colorBackRgb(RgbColorConverter.fromHex(lineVersionModel.getColorBackRgb()))
                      .colorFontCmyk(
                          CmykColorConverter.fromCmykString(lineVersionModel.getColorFontCmyk()))
                      .colorBackCmyk(
                          CmykColorConverter.fromCmykString(lineVersionModel.getColorBackCmyk()))
                      .description(lineVersionModel.getDescription())
                      .icon(lineVersionModel.getIcon())
                      .validFrom(lineVersionModel.getValidFrom())
                      .validTo(lineVersionModel.getValidTo())
                      .businessOrganisation(lineVersionModel.getBusinessOrganisation())
                      .comment(lineVersionModel.getComment())
                      .swissLineNumber(lineVersionModel.getSwissLineNumber())
                      .version(lineVersionModel.getEtagVersion())
                      .build();
  }

}
