package ch.sbb.line.directory.controller;

import ch.sbb.atlas.api.lidi.CoverageModel;
import ch.sbb.atlas.api.lidi.ReadSublineVersionModelV2;
import ch.sbb.atlas.api.lidi.SublineApiV1;
import ch.sbb.atlas.api.lidi.SublineVersionModel;
import ch.sbb.atlas.api.lidi.SublineVersionModelV2;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.mapper.CoverageMapper;
import ch.sbb.line.directory.mapper.SublineMapper;
import ch.sbb.line.directory.service.CoverageService;
import ch.sbb.line.directory.service.SublineService;
import ch.sbb.line.directory.service.export.SublineVersionExportService;
import java.net.URL;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SublineController implements SublineApiV1 {

  private final SublineService sublineService;
  private final CoverageService coverageService;

  private final SublineVersionExportService sublineVersionExportService;

   @Override
  public List<SublineVersionModel> getSublineVersion(String slnid) {
    List<SublineVersionModel> sublineVersionModels = sublineService.findSubline(slnid)
        .stream()
        .map(this::toModel)
        .toList();
    if (sublineVersionModels.isEmpty()) {
      throw new SlnidNotFoundException(slnid);
    }
    return sublineVersionModels;
  }

  @Override
  public List<SublineVersionModel> revokeSubline(String slnid) {
    List<SublineVersionModel> sublineVersionModels = sublineService.revokeSubline(slnid).stream()
        .map(this::toModel)
        .toList();
    if (sublineVersionModels.isEmpty()) {
      throw new SlnidNotFoundException(slnid);
    }
    return sublineVersionModels;
  }

  @Override
  public ReadSublineVersionModelV2 createSublineVersion(SublineVersionModelV2 newSublineVersion) {
    SublineVersion sublineVersion = SublineMapper.toEntity(newSublineVersion);
    sublineVersion.setStatus(Status.VALIDATED);
    SublineVersion createdVersion = sublineService.create(sublineVersion);

    LineVersion lineVersion = sublineService.getMainLineVersion(sublineVersion.getMainlineSlnid());
    return SublineMapper.toModel(createdVersion, lineVersion);
  }

  @Override
  public List<ReadSublineVersionModelV2> updateSublineVersion(Long id, SublineVersionModelV2 newVersion) {
    SublineVersion versionToUpdate = sublineService.findById(id)
        .orElseThrow(() -> new IdNotFoundException(id));
    sublineService.update(versionToUpdate, SublineMapper.toEntity(newVersion),
        sublineService.findSubline(versionToUpdate.getSlnid()));

    LineVersion lineVersion = sublineService.getMainLineVersion(versionToUpdate.getMainlineSlnid());
    return sublineService.findSubline(versionToUpdate.getSlnid()).stream().map(i -> SublineMapper.toModel(i, lineVersion))
        .toList();
  }

  @Override
  public CoverageModel getSublineCoverage(String slnid) {
    return CoverageMapper.toModel(
        coverageService.getSublineCoverageBySlnidAndSublineModelType(slnid));
  }

  @Override
  public List<URL> exportFullSublineVersions() {
    return sublineVersionExportService.exportFullVersions();
  }

  @Override
  public List<URL> exportActualSublineVersions() {
    return sublineVersionExportService.exportActualVersions();
  }

  @Override
  public List<URL> exportFutureTimetableSublineVersions() {
    return sublineVersionExportService.exportFutureTimetableVersions();
  }

  @Override
  public void deleteSublines(String slnid) {
    sublineService.deleteAll(slnid);
  }

  private SublineVersionModel toModel(SublineVersion sublineVersion) {
    return SublineVersionModel.builder()
        .id(sublineVersion.getId())
        .swissSublineNumber(sublineVersion.getSwissSublineNumber())
        .mainlineSlnid(sublineVersion.getMainlineSlnid())
        .status(sublineVersion.getStatus())
        .sublineType(sublineVersion.getSublineType())
        .slnid(sublineVersion.getSlnid())
        .description(sublineVersion.getDescription())
        .number(sublineVersion.getNumber())
        .longName(sublineVersion.getLongName())
        .paymentType(sublineVersion.getPaymentType())
        .validFrom(sublineVersion.getValidFrom())
        .validTo(sublineVersion.getValidTo())
        .businessOrganisation(sublineVersion.getBusinessOrganisation())//
        .etagVersion(sublineVersion.getVersion())
        .creator(sublineVersion.getCreator())
        .creationDate(sublineVersion.getCreationDate())
        .editor(sublineVersion.getEditor())
        .editionDate(sublineVersion.getEditionDate())
        .build();
  }

}
