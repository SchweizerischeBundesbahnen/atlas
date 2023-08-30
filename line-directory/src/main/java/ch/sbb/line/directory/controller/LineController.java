package ch.sbb.line.directory.controller;

import static java.util.stream.Collectors.toSet;

import ch.sbb.atlas.api.lidi.CoverageModel;
import ch.sbb.atlas.api.lidi.LineApiV1;
import ch.sbb.atlas.api.lidi.LineModel;
import ch.sbb.atlas.api.lidi.LineVersionModel;
import ch.sbb.atlas.api.lidi.LineVersionSnapshotModel;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.line.directory.converter.CmykColorConverter;
import ch.sbb.line.directory.converter.RgbColorConverter;
import ch.sbb.line.directory.entity.Line;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.LineVersionSnapshot;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.mapper.CoverageMapper;
import ch.sbb.line.directory.mapper.LineVersionSnapshotMapper;
import ch.sbb.line.directory.mapper.LineVersionWorkflowMapper;
import ch.sbb.line.directory.model.search.LineSearchRestrictions;
import ch.sbb.line.directory.model.search.LineVersionSnapshotSearchRestrictions;
import ch.sbb.line.directory.service.CoverageService;
import ch.sbb.line.directory.service.LineService;
import ch.sbb.line.directory.service.LineVersionSnapshotService;
import ch.sbb.line.directory.service.export.LineVersionExportService;
import java.net.URL;
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
  private final LineVersionExportService lineVersionExportService;

  private final LineVersionSnapshotService lineVersionSnapshotService;

  @Override
  public Container<LineModel> getLines(Pageable pageable, Optional<String> swissLineNumber,
      List<String> searchCriteria, List<Status> statusRestrictions, List<LineType> typeRestrictions,
      Optional<String> businessOrganisation,
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
        .businessOrganisation(
            businessOrganisation)
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
  public List<LineVersionModel> revokeLine(String slnid) {
    List<LineVersionModel> lineVersionModels = lineService.revokeLine(slnid).stream()
        .map(this::toModel)
        .toList();
    if (lineVersionModels.isEmpty()) {
      throw new SlnidNotFoundException(slnid);
    }
    return lineVersionModels;
  }

  @Override
  public List<LineModel> getCoveredLines() {
    return lineService.getAllCoveredLines().stream().map(this::toModel).toList();
  }

  @Override
  public List<LineVersionModel> getCoveredVersionLines() {
    return lineService.getAllCoveredLineVersions().stream().map(this::toModel).toList();
  }

  @Override
  public List<LineVersionModel> getLineVersions(String slnid) {
    List<LineVersionModel> lineVersionModels = lineService.findLineVersions(slnid).stream()
        .map(this::toModel)
        .toList();
    if (lineVersionModels.isEmpty()) {
      throw new SlnidNotFoundException(slnid);
    }
    return lineVersionModels;
  }

  @Override
  public LineVersionModel createLineVersion(LineVersionModel newVersion) {
    LineVersion newLineVersion = toEntity(newVersion);
    newLineVersion.setStatus(Status.VALIDATED);
    LineVersion createdVersion = lineService.create(newLineVersion);
    return toModel(createdVersion);
  }

  @Override
  public List<LineVersionModel> updateLineVersion(Long id, LineVersionModel newVersion) {
    LineVersion versionToUpdate = lineService.findById(id)
        .orElseThrow(() -> new IdNotFoundException(id));
    lineService.update(versionToUpdate, toEntity(newVersion), lineService.findLineVersions(
        versionToUpdate.getSlnid()));
    return lineService.findLineVersions(versionToUpdate.getSlnid()).stream().map(this::toModel)
        .toList();
  }

  @Override
  public void skipWorkflow(Long id) {
    lineService.skipWorkflow(id);
  }

  @Override
  public CoverageModel getLineCoverage(String slnid) {
    return CoverageMapper.toModel(coverageService.getSublineCoverageBySlnidAndLineModelType(slnid));
  }

  @Override
  public List<URL> exportFullLineVersions() {
    return lineVersionExportService.exportFullVersions();
  }

  @Override
  public List<URL> exportActualLineVersions() {
    return lineVersionExportService.exportActualVersions();
  }

  @Override
  public List<URL> exportFutureTimetableLineVersions() {
    return lineVersionExportService.exportFutureTimetableVersions();
  }

  @Override
  public Container<LineVersionSnapshotModel> getLineVersionSnapshot(Pageable pageable, List<String> searchCriteria,
      Optional<LocalDate> validOn, List<WorkflowStatus> statusChoices) {
    log.info(
        "Load LineVersionSnapshot using pageable={}, searchCriteriaSpecification={}, validOn={}", pageable, searchCriteria,
        validOn);
    Page<LineVersionSnapshot> lineVersionSnapshotPage = lineVersionSnapshotService.findAll(
        LineVersionSnapshotSearchRestrictions.builder()
            .pageable(pageable)
            .searchCriterias(searchCriteria)
            .statusRestrictions(statusChoices)
            .validOn(validOn)
            .build());
    List<LineVersionSnapshotModel> lineVersionSnapshotModels = lineVersionSnapshotPage.stream()
        .map(LineVersionSnapshotMapper::toModel).toList();
    return Container.<LineVersionSnapshotModel>builder()
        .objects(lineVersionSnapshotModels)
        .totalCount(lineVersionSnapshotPage.getTotalElements())
        .build();
  }

  @Override
  public LineVersionSnapshotModel getLineVersionSnapshotById(Long id) {
    return LineVersionSnapshotMapper.toModel(lineVersionSnapshotService.getLineVersionSnapshotById(id));
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
        .creationDate(lineVersionModel.getCreationDate())
        .creator(lineVersionModel.getCreator())
        .editionDate(lineVersionModel.getEditionDate())
        .editor(lineVersionModel.getEditor())
        .version(lineVersionModel.getEtagVersion())
        .build();
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

}
