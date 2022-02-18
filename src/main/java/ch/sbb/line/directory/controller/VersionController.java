package ch.sbb.line.directory.controller;

import ch.sbb.line.directory.api.Container;
import ch.sbb.line.directory.api.TimetableFieldNumberApiV1;
import ch.sbb.line.directory.api.TimetableFieldNumberModel;
import ch.sbb.line.directory.api.VersionModel;
import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.service.VersionService;
import ch.sbb.line.directory.entity.Version;
import ch.sbb.line.directory.exception.NotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class VersionController implements TimetableFieldNumberApiV1 {

  private final VersionService versionService;

  @Autowired
  public VersionController(VersionService versionService) {
    this.versionService = versionService;
  }

  @Override
  public Container<TimetableFieldNumberModel> getOverview(Pageable pageable, List<String> searchCriteria,
      LocalDate validOn, List<Status> statusChoices) {
    log.info(
        "Load TimetableFieldNumbers using pageable={}, searchCriteria={}, validOn={} and statusChoices={}",
        pageable, searchCriteria, validOn, statusChoices);
    Page<TimetableFieldNumber> timetableFieldNumberPage = versionService.getVersionsSearched(
        pageable,
        searchCriteria,
        validOn, statusChoices);
    List<TimetableFieldNumberModel> versions = timetableFieldNumberPage.stream().map(this::toModel)
                                                                       .collect(
                                                                           Collectors.toList());
    return Container.<TimetableFieldNumberModel>builder()
                                        .objects(versions)
                                        .totalCount(timetableFieldNumberPage.getTotalElements())
                                        .build();
  }

  private TimetableFieldNumberModel toModel(TimetableFieldNumber version) {
    return TimetableFieldNumberModel.builder()
                                    .description(version.getDescription())
                                    .ttfnid(version.getTtfnid())
                                    .swissTimetableFieldNumber(
                                        version.getSwissTimetableFieldNumber())
                                    .status(version.getStatus())
                                    .validFrom(version.getValidFrom())
                                    .validTo(version.getValidTo())
                                    .build();
  }

  @Override
  public VersionModel getVersion(Long id) {
    return versionService.findById(id)
                         .map(this::toModel)
                         .orElseThrow(() ->
                             new NotFoundException(NotFoundException.ID, String.valueOf(id)));
  }

  @Override
  public List<VersionModel> getAllVersionsVersioned(String ttfnId) {
    List<VersionModel> versionModels = versionService.getAllVersionsVersioned(ttfnId)
                                               .stream()
                                               .map(this::toModel)
                                               .collect(Collectors.toList());
    if (versionModels.isEmpty()){
      throw new NotFoundException("ttfnId", ttfnId);
    }
    return versionModels;
  }

  @Override
  public VersionModel createVersion(VersionModel newVersion) {
    newVersion.setStatus(Status.ACTIVE);
    Version createdVersion = versionService.save(toEntity(newVersion));
    return toModel(createdVersion);
  }

  @Override
  public List<VersionModel> updateVersionWithVersioning(Long id, VersionModel newVersion) {
    Version versionToUpdate = versionService.findById(id).orElseThrow(() ->
        new NotFoundException(NotFoundException.ID, String.valueOf(id)));
    versionService.updateVersion(versionToUpdate, toEntity(newVersion));
    return getAllVersionsVersioned(versionToUpdate.getTtfnid());
  }

  @Override
  public void deleteVersions(String ttfnid) {
    List<Version> allVersionsVersioned = versionService.getAllVersionsVersioned(ttfnid);
    if (allVersionsVersioned.isEmpty()) {
      throw new NotFoundException("ttfnid", ttfnid);
    }
    versionService.deleteAll(allVersionsVersioned);
  }

  private VersionModel toModel(Version version) {
    return VersionModel.builder()
                       .id(version.getId())
                       .description(version.getDescription())
                       .number(version.getNumber())
                       .ttfnid(version.getTtfnid())
                       .swissTimetableFieldNumber(version.getSwissTimetableFieldNumber())
                       .status(version.getStatus())
                       .validFrom(version.getValidFrom())
                       .validTo(version.getValidTo())
                       .businessOrganisation(version.getBusinessOrganisation())
                       .comment(version.getComment())
                       .etagVersion(version.getVersion())
                       .build();
  }

  private Version toEntity(VersionModel versionModel) {
    return Version.builder()
                  .id(versionModel.getId())
                  .description(versionModel.getDescription())
                  .number(versionModel.getNumber())
                  .swissTimetableFieldNumber(versionModel.getSwissTimetableFieldNumber())
                  .status(versionModel.getStatus())
                  .validFrom(versionModel.getValidFrom())
                  .validTo(versionModel.getValidTo())
                  .businessOrganisation(versionModel.getBusinessOrganisation())
                  .comment(versionModel.getComment())
                  .version(versionModel.getEtagVersion())
                  .build();
  }
}
