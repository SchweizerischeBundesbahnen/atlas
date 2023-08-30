package ch.sbb.line.directory.controller;

import ch.sbb.atlas.api.lidi.CoverageModel;
import ch.sbb.atlas.api.lidi.SublineApiV1;
import ch.sbb.atlas.api.lidi.SublineModel;
import ch.sbb.atlas.api.lidi.SublineVersionModel;
import ch.sbb.atlas.api.lidi.enumaration.SublineType;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.line.directory.entity.Subline;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.mapper.CoverageMapper;
import ch.sbb.line.directory.model.search.SublineSearchRestrictions;
import ch.sbb.line.directory.service.CoverageService;
import ch.sbb.line.directory.service.SublineService;
import ch.sbb.line.directory.service.export.SublineVersionExportService;
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
public class SublineController implements SublineApiV1 {

  private final SublineService sublineService;
  private final CoverageService coverageService;

  private final SublineVersionExportService sublineVersionExportService;

  @Override
  public Container<SublineModel> getSublines(Pageable pageable, List<String> searchCriteria,
      List<Status> statusRestrictions, List<SublineType> typeRestrictions,
      Optional<String> businessOrganisation,
      Optional<LocalDate> validOn) {
    log.info("Load Versions using pageable={}", pageable);
    Page<Subline> sublines = sublineService.findAll(SublineSearchRestrictions.builder()
        .pageable(pageable)
        .searchCriterias(searchCriteria)
        .statusRestrictions(statusRestrictions)
        .validOn(validOn)
        .typeRestrictions(
            typeRestrictions)
        .businessOrganisation(
            businessOrganisation)
        .build());
    return Container.<SublineModel>builder()
        .objects(sublines.stream().map(this::toModel).toList())
        .totalCount(sublines.getTotalElements())
        .build();
  }

  private SublineModel toModel(Subline sublineVersion) {
    return SublineModel.builder()
        .swissSublineNumber(sublineVersion.getSwissSublineNumber())
        .number(sublineVersion.getNumber())
        .swissLineNumber(sublineVersion.getSwissLineNumber())
        .status(sublineVersion.getStatus())
        .sublineType(sublineVersion.getSublineType())
        .slnid(sublineVersion.getSlnid())
        .description(sublineVersion.getDescription())
        .validFrom(sublineVersion.getValidFrom())
        .validTo(sublineVersion.getValidTo())
        .businessOrganisation(sublineVersion.getBusinessOrganisation())
        .build();
  }

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
  public SublineVersionModel createSublineVersion(SublineVersionModel newSublineVersion) {
    SublineVersion sublineVersion = toEntity(newSublineVersion);
    sublineVersion.setStatus(Status.VALIDATED);
    SublineVersion createdVersion = sublineService.create(sublineVersion);
    return toModel(createdVersion);
  }

  @Override
  public List<SublineVersionModel> updateSublineVersion(Long id, SublineVersionModel newVersion) {
    SublineVersion versionToUpdate = sublineService.findById(id)
        .orElseThrow(() -> new IdNotFoundException(id));
    sublineService.update(versionToUpdate, toEntity(newVersion), sublineService.findSubline(
        versionToUpdate.getSlnid()));
    return sublineService.findSubline(versionToUpdate.getSlnid()).stream().map(this::toModel)
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
        .businessOrganisation(sublineVersion.getBusinessOrganisation())
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
