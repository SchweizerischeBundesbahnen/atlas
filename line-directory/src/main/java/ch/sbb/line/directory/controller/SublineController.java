package ch.sbb.line.directory.controller;

import ch.sbb.atlas.api.lidi.SublineApiV1;
import ch.sbb.atlas.api.lidi.SublineVersionModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
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
  public void revokeSubline(String slnid) {
    sublineService.revokeSubline(slnid);
  }

  public SublineVersionModel createSublineVersion(SublineVersionModel newSublineVersion) {
    SublineVersion sublineVersion = toEntity(newSublineVersion);
    sublineVersion.setStatus(Status.VALIDATED);
    SublineVersion createdVersion = sublineService.create(sublineVersion);
    return toModel(createdVersion);
  }

  @Deprecated(forRemoval = true)
  @Override
  public List<URL> exportFullSublineVersions() {
    return sublineVersionExportService.exportFullVersions();
  }

  @Deprecated(forRemoval = true)
  @Override
  public List<URL> exportActualSublineVersions() {
    return sublineVersionExportService.exportActualVersions();
  }

  @Deprecated(forRemoval = true)
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

  private SublineVersion toEntity(SublineVersionModel sublineVersionModel) {
    return SublineVersion.builder()
        .id(sublineVersionModel.getId())
        .swissSublineNumber(sublineVersionModel.getSwissSublineNumber())
        .mainlineSlnid(sublineVersionModel.getMainlineSlnid())
        .sublineType(sublineVersionModel.getSublineType())
        .slnid(sublineVersionModel.getSlnid())
        .description(sublineVersionModel.getDescription())
        .number(sublineVersionModel.getNumber())
        .longName(sublineVersionModel.getLongName())
        .paymentType(sublineVersionModel.getPaymentType())
        .validFrom(sublineVersionModel.getValidFrom())
        .validTo(sublineVersionModel.getValidTo())
        .businessOrganisation(sublineVersionModel.getBusinessOrganisation())
        .creationDate(sublineVersionModel.getCreationDate())
        .creator(sublineVersionModel.getCreator())
        .editionDate(sublineVersionModel.getEditionDate())
        .editor(sublineVersionModel.getEditor())
        .version(sublineVersionModel.getEtagVersion())
        .build();
  }
}
