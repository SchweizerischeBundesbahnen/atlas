package ch.sbb.line.directory.controller;

import ch.sbb.line.directory.entity.Version;
import ch.sbb.line.directory.api.VersionApi;
import ch.sbb.line.directory.api.VersionModel;
import ch.sbb.line.directory.api.VersionsContainer;
import ch.sbb.line.directory.repository.VersionRepository;
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

  private final VersionRepository versionRepository;

  @Autowired
  public VersionController(VersionRepository versionRepository) {
    this.versionRepository = versionRepository;
  }

  @Override
  public VersionsContainer getVersions(Pageable pageable) {
    log.info("Load Versions using pageable={}", pageable);
    List<VersionModel> versions = versionRepository.findAll(pageable).stream().map(this::toModel)
                                                  .collect(Collectors.toList());
    long totalCount = versionRepository.count();
    return VersionsContainer.builder()
                            .versions(versions)
                            .totalCount(totalCount).build();
  }

  @Override
  public VersionModel getVersion(Long id) {
    return versionRepository.findById(id).map(this::toModel).orElseThrow(NOT_FOUND_EXCEPTION);
  }

  @Override
  public VersionModel createVersion(VersionModel newVersion) {
    Version createdVersion = versionRepository.save(toEntity(newVersion));
    return toModel(createdVersion);
  }

  @Override
  public VersionModel updateVersion(Long id, VersionModel newVersion) {
    Version versionToUpdate = versionRepository.findById(id).orElseThrow(NOT_FOUND_EXCEPTION);

    versionToUpdate.setTtfnid(newVersion.getTtfnid());
    versionToUpdate.setName(newVersion.getName());
    versionToUpdate.setNumber(newVersion.getNumber());
    versionToUpdate.setSwissTimetableFieldNumber(newVersion.getSwissTimetableFieldNumber());
    versionToUpdate.setValidFrom(newVersion.getValidFrom());
    versionToUpdate.setValidTo(newVersion.getValidTo());
    versionToUpdate.setComment(newVersion.getComment());
    versionToUpdate.setBusinessOrganisation(newVersion.getBusinessOrganisation());
    versionToUpdate.setNameCompact(newVersion.getNameCompact());
    versionRepository.save(versionToUpdate);

    return toModel(versionToUpdate);
  }

  @Override
  public void deleteVersion(Long id) {
    if (!versionRepository.existsById(id)) {
      throw NOT_FOUND_EXCEPTION.get();
    }
    versionRepository.deleteById(id);
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
