package ch.sbb.line.directory.controller;

import static java.util.stream.Collectors.toSet;

import ch.sbb.atlas.api.lidi.LineApiV2;
import ch.sbb.atlas.api.lidi.LineVersionModelV2;
import ch.sbb.atlas.api.lidi.UpdateLineVersionModelV2;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.mapper.LineMapper;
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
    List<LineVersion> versions = lineService.findLineVersions(slnid);
    if (versions.isEmpty()) {
      throw new SlnidNotFoundException(slnid);
    }
    return versions.stream().map(this::toModel).toList();
  }

  @Override
  public LineVersionModelV2 createLineVersionV2(LineVersionModelV2 newVersion) {
    LineVersion newLineVersion = toEntity(newVersion);
    newLineVersion.setStatus(Status.VALIDATED);
    LineVersion createdVersion = lineService.createV2(newLineVersion);
    return toModel(createdVersion);
  }

  @Override
  public List<LineVersionModelV2> updateLineVersion(Long id, UpdateLineVersionModelV2 newVersion) {
    LineVersion versionToUpdate = lineService.getLineVersionById(id);
    lineService.update(versionToUpdate, LineMapper.toEntityFromUpdate(newVersion, versionToUpdate), lineService.findLineVersions(
        versionToUpdate.getSlnid()));
    return lineService.findLineVersions(versionToUpdate.getSlnid()).stream().map(this::toModel)
        .toList();
  }

  private LineVersionModelV2 toModel(LineVersion lineVersion) {
    LineVersionModelV2 lineVersionModelV2 = LineVersionModelV2.builder()
        .id(lineVersion.getId())
        .status(lineVersion.getStatus())
        .lineType(lineVersion.getLineType())
        .slnid(lineVersion.getSlnid())
        .number(lineVersion.getNumber())
        .longName(lineVersion.getLongName())
        .shortNumber(lineVersion.getShortNumber())
        .offerCategory(lineVersion.getOfferCategory())
        .description(lineVersion.getDescription())
        .validFrom(lineVersion.getValidFrom())
        .validTo(lineVersion.getValidTo())
        .businessOrganisation(lineVersion.getBusinessOrganisation())
        .comment(lineVersion.getComment())
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
    if (lineVersion.getLineType() == LineType.ORDERLY) {
      lineVersionModelV2.setSwissLineNumber(lineVersion.getSwissLineNumber());
      lineVersionModelV2.setLineConcessionType(lineVersion.getConcessionType());
    }
    return lineVersionModelV2;
  }

  private LineVersion toEntity(LineVersionModelV2 lineVersionModel) {
    return LineVersion.builder()
        .id(lineVersionModel.getId())
        .lineType(lineVersionModel.getLineType())
        .slnid(lineVersionModel.getSlnid())
        .number(lineVersionModel.getNumber())
        .longName(lineVersionModel.getLongName())
        .concessionType(lineVersionModel.getLineConcessionType())
        .shortNumber(lineVersionModel.getShortNumber())
        .offerCategory(lineVersionModel.getOfferCategory())
        .description(lineVersionModel.getDescription())
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

}
