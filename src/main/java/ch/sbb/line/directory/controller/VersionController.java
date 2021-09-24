package ch.sbb.line.directory.controller;

import ch.sbb.line.directory.api.SublineVersionModel;
import ch.sbb.line.directory.api.VersionApi;
import ch.sbb.line.directory.api.VersionModel;
import ch.sbb.line.directory.api.VersionsContainer;
import ch.sbb.line.directory.converter.ColorConverter;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.entity.Version;
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

    versionToUpdate.setSublineVersions(
        newVersion.getSublineVersions().stream().map(this::toEntity).collect(
            Collectors.toSet()));
    versionToUpdate.setStatus(newVersion.getStatus());
    versionToUpdate.setType(newVersion.getType());
    versionToUpdate.setSlnid(newVersion.getSlnid());
    versionToUpdate.setPaymentType(newVersion.getPaymentType());
    versionToUpdate.setShortName(newVersion.getShortName());
    versionToUpdate.setAlternativeName(newVersion.getAlternativeName());
    versionToUpdate.setCombinationName(newVersion.getCombinationName());
    versionToUpdate.setLongName(newVersion.getLongName());
    versionToUpdate.setColorFontRgb(ColorConverter.fromHexString(newVersion.getColorFontRgb()));
    versionToUpdate.setColorBackRgb(ColorConverter.fromHexString(newVersion.getColorBackRgb()));
    versionToUpdate.setColorFontCmyk(ColorConverter.fromHexString(newVersion.getColorFontCmyk()));
    versionToUpdate.setColorBackCmyk(ColorConverter.fromHexString(newVersion.getColorBackCmyk()));
    versionToUpdate.setDescription(newVersion.getDescription());
    versionToUpdate.setValidFrom(newVersion.getValidFrom());
    versionToUpdate.setValidTo(newVersion.getValidTo());
    versionToUpdate.setBusinessOrganisation(newVersion.getBusinessOrganisation());
    versionToUpdate.setComment(newVersion.getComment());
    versionToUpdate.setSwissLineNumber(newVersion.getSwissLineNumber());
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
                       .sublineVersions(version.getSublineVersions()
                                               .stream()
                                               .map(this::toModel)
                                               .collect(Collectors.toSet()))
                       .status(version.getStatus())
                       .type(version.getType())
                       .slnid(version.getSlnid())
                       .paymentType(version.getPaymentType())
                       .shortName(version.getShortName())
                       .alternativeName(version.getAlternativeName())
                       .combinationName(version.getCombinationName())
                       .longName(version.getLongName())
                       .colorFontRgb(ColorConverter.toHexString(version.getColorFontRgb()))
                       .colorBackRgb(ColorConverter.toHexString(version.getColorBackRgb()))
                       .colorFontCmyk(ColorConverter.toHexString(version.getColorFontCmyk()))
                       .colorBackCmyk(ColorConverter.toHexString(version.getColorBackCmyk()))
                       .description(version.getDescription())
                       .validFrom(version.getValidFrom())
                       .validTo(version.getValidTo())
                       .businessOrganisation(version.getBusinessOrganisation())
                       .comment(version.getComment())
                       .swissLineNumber(version.getSwissLineNumber())
                       .build();
  }

  private SublineVersionModel toModel(SublineVersion sublineVersion) {
    return SublineVersionModel.builder()
                              .id(sublineVersion.getId())
                              .type(sublineVersion.getType())
                              .slnid(sublineVersion.getSlnid())
                              .description(sublineVersion.getDescription())
                              .shortName(sublineVersion.getShortName())
                              .longName(sublineVersion.getLongName())
                              .paymentType(sublineVersion.getPaymentType())
                              .validFrom(sublineVersion.getValidFrom())
                              .validTo(sublineVersion.getValidTo())
                              .businessOrganisation(sublineVersion.getBusinessOrganisation())
                              .build();
  }

  private Version toEntity(VersionModel versionModel) {
    return Version.builder()
                  .id(versionModel.getId())
                  .sublineVersions(versionModel.getSublineVersions()
                                               .stream()
                                               .map(this::toEntity)
                                               .collect(Collectors.toSet()))
                  .status(versionModel.getStatus())
                  .type(versionModel.getType())
                  .slnid(versionModel.getSlnid())
                  .paymentType(versionModel.getPaymentType())
                  .shortName(versionModel.getShortName())
                  .alternativeName(versionModel.getAlternativeName())
                  .combinationName(versionModel.getCombinationName())
                  .longName(versionModel.getLongName())
                  .colorFontRgb(ColorConverter.fromHexString(versionModel.getColorFontRgb()))
                  .colorBackRgb(ColorConverter.fromHexString(versionModel.getColorBackRgb()))
                  .colorFontCmyk(ColorConverter.fromHexString(versionModel.getColorFontCmyk()))
                  .colorBackCmyk(ColorConverter.fromHexString(versionModel.getColorBackCmyk()))
                  .description(versionModel.getDescription())
                  .validFrom(versionModel.getValidFrom())
                  .validTo(versionModel.getValidTo())
                  .businessOrganisation(versionModel.getBusinessOrganisation())
                  .comment(versionModel.getComment())
                  .swissLineNumber(versionModel.getSwissLineNumber())
                  .build();
  }

  private SublineVersion toEntity(SublineVersionModel sublineVersionModel) {
    return SublineVersion.builder()
                         .id(sublineVersionModel.getId())
                         .type(sublineVersionModel.getType())
                         .slnid(sublineVersionModel.getSlnid())
                         .description(sublineVersionModel.getDescription())
                         .shortName(sublineVersionModel.getShortName())
                         .longName(sublineVersionModel.getLongName())
                         .paymentType(sublineVersionModel.getPaymentType())
                         .validFrom(sublineVersionModel.getValidFrom())
                         .validTo(sublineVersionModel.getValidTo())
                         .businessOrganisation(sublineVersionModel.getBusinessOrganisation())
                         .build();
  }
}
