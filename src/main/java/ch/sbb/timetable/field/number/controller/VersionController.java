package ch.sbb.timetable.field.number.controller;

import ch.sbb.timetable.field.number.api.VersionApi;
import ch.sbb.timetable.field.number.api.VersionModel;
import ch.sbb.timetable.field.number.api.VersionsContainer;
import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.service.VersionService;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Slf4j
public class VersionController implements VersionApi {

  private static final Supplier<ResponseStatusException> NOT_FOUND_EXCEPTION = () -> new ResponseStatusException(
      HttpStatus.NOT_FOUND);

  private final VersionService versionService;

  @Autowired
  public VersionController(VersionService versionService) {
    this.versionService = versionService;
  }

  @Override
  public VersionsContainer getVersions(Pageable pageable) {
    log.info("Load Versions using pageable={}", pageable);
    List<VersionModel> versions = versionService.findAll(pageable).stream().map(this::toModel)
                                                  .collect(Collectors.toList());
    long totalCount = versionService.count();
    return VersionsContainer.builder()
                            .versions(versions)
                            .totalCount(totalCount).build();
  }

  @Override
  public VersionModel getVersion(Long id) {
    return versionService.findById(id).map(this::toModel).orElseThrow(NOT_FOUND_EXCEPTION);
  }

  @Override
  public List<VersionModel> getAllVersionsVersioned(String ttfnId) {
    List<VersionModel> versionModels = new ArrayList<>();
    List<Version> allVersionsVersioned = versionService.getAllVersionsVersioned(ttfnId);
    for (Version version: allVersionsVersioned){
      VersionModel versionModel = toModel(version);
      versionModels.add(versionModel);
    }
    return versionModels;
  }

  @Override
  public VersionModel createVersion(VersionModel newVersion) {
    Version createdVersion = versionService.save(toEntity(newVersion));
    return toModel(createdVersion);
  }

  @Override
  public VersionModel updateVersion(Long id, VersionModel newVersion) {
    Version versionToUpdate = versionService.findById(id).orElseThrow(NOT_FOUND_EXCEPTION);

    versionToUpdate.setTtfnid(newVersion.getTtfnid());
    versionToUpdate.setName(newVersion.getName());
    versionToUpdate.setNumber(newVersion.getNumber());
    versionToUpdate.setSwissTimetableFieldNumber(newVersion.getSwissTimetableFieldNumber());
    versionToUpdate.setValidFrom(newVersion.getValidFrom());
    versionToUpdate.setValidTo(newVersion.getValidTo());
    versionToUpdate.setComment(newVersion.getComment());
    versionToUpdate.setBusinessOrganisation(newVersion.getBusinessOrganisation());
    versionToUpdate.setNameCompact(newVersion.getNameCompact());
    versionService.save(versionToUpdate);

    return toModel(versionToUpdate);
  }

  @Override
  public List<VersionModel> updateVersionWithVersioning(Long id, VersionModel newVersion) {
    Version versionToUpdate = versionService.findById(id).orElseThrow(NOT_FOUND_EXCEPTION);
    versionService.updateVersion(versionToUpdate,toEntity(newVersion));
    return getAllVersionsVersioned(versionToUpdate.getTtfnid());
  }

  @Override
  public void deleteVersion(Long id) {
    if (!versionService.existsById(id)) {
      throw NOT_FOUND_EXCEPTION.get();
    }
    versionService.deleteById(id);
  }

  private VersionModel toModel(Version version) {
    return VersionModel.builder()
                       .id(version.getId())
                       .name(version.getName())
                       .number(version.getNumber())
                       .ttfnid(version.getTtfnid())
                       .swissTimetableFieldNumber(version.getSwissTimetableFieldNumber())
                       .status(version.getStatus())
                       .validFrom(version.getValidFrom())
                       .validTo(version.getValidTo())
                       .businessOrganisation(version.getBusinessOrganisation())
                       .comment(version.getComment())
                       .nameCompact(version.getNameCompact())
                       .build();
  }

  private Version toEntity(VersionModel versionModel) {
    return Version.builder()
                  .id(versionModel.getId())
                  .name(versionModel.getName())
                  .number(versionModel.getNumber())
                  .ttfnid(versionModel.getTtfnid())
                  .swissTimetableFieldNumber(versionModel.getSwissTimetableFieldNumber())
                  .status(versionModel.getStatus())
                  .validFrom(versionModel.getValidFrom())
                  .validTo(versionModel.getValidTo())
                  .businessOrganisation(versionModel.getBusinessOrganisation())
                  .comment(versionModel.getComment())
                  .nameCompact(versionModel.getNameCompact())
                  .build();
  }
}
