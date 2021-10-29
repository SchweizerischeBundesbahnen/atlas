package ch.sbb.line.directory.controller;

import ch.sbb.line.directory.api.SublineVersionApi;
import ch.sbb.line.directory.api.SublineVersionModel;
import ch.sbb.line.directory.api.VersionsContainer;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.service.SublineService;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SublineVersionController implements SublineVersionApi {

  private final SublineService sublineService;

  @Override
  public SublineVersionModel getSublineVersion(Long id) {
    return sublineService.findById(id)
                         .map(SublineVersionController::toModel)
                         .orElseThrow(NotFoundExcpetion.getInstance());
  }

  public VersionsContainer<SublineVersionModel> getSublineVersions(Pageable pageable) {
    List<SublineVersionModel> sublineVersions = toModel(sublineService.findAll(
        pageable));
    return VersionsContainer.<SublineVersionModel>builder()
                            .versions(sublineVersions)
                            .totalCount(sublineService.totalCount())
                            .build();
  }

  @Override
  public SublineVersionModel createSublineVersion(SublineVersionModel newSublineVersion) {
    SublineVersion createdVersion = sublineService.save(toEntity(newSublineVersion));
    return toModel(createdVersion);
  }

  @Override
  public SublineVersionModel updateSublineVersion(Long id, SublineVersionModel newVersion) {
    SublineVersion versionToUpdate = sublineService.findById(id)
                                                   .orElseThrow(
                                                       NotFoundExcpetion.getInstance());
    versionToUpdate.setSwissSublineNumber(newVersion.getSwissSublineNumber());
    versionToUpdate.setSwissLineNumber(newVersion.getSwissLineNumber());
    versionToUpdate.setStatus(newVersion.getStatus());
    versionToUpdate.setType(newVersion.getType());
    versionToUpdate.setSlnid(newVersion.getSlnid());
    versionToUpdate.setPaymentType(newVersion.getPaymentType());
    versionToUpdate.setNumber(newVersion.getNumber());
    versionToUpdate.setLongName(newVersion.getLongName());
    versionToUpdate.setDescription(newVersion.getDescription());
    versionToUpdate.setValidFrom(newVersion.getValidFrom());
    versionToUpdate.setValidTo(newVersion.getValidTo());
    versionToUpdate.setBusinessOrganisation(newVersion.getBusinessOrganisation());
    sublineService.save(versionToUpdate);

    return toModel(versionToUpdate);
  }

  @Override
  public void deleteSublineVersion(Long id) {
    sublineService.deleteById(id);
  }

  private static List<SublineVersionModel> toModel(Iterable<SublineVersion> versions) {
    return StreamSupport.stream(versions.spliterator(), false)
                        .map(SublineVersionController::toModel)
                        .collect(Collectors.toList());
  }

  static SublineVersionModel toModel(SublineVersion sublineVersion) {
    return SublineVersionModel.builder()
                              .id(sublineVersion.getId())
                              .swissSublineNumber(sublineVersion.getSwissSublineNumber())
                              .swissLineNumber(sublineVersion.getSwissLineNumber())
                              .status(sublineVersion.getStatus())
                              .type(sublineVersion.getType())
                              .slnid(sublineVersion.getSlnid())
                              .description(sublineVersion.getDescription())
                              .number(sublineVersion.getNumber())
                              .longName(sublineVersion.getLongName())
                              .paymentType(sublineVersion.getPaymentType())
                              .validFrom(sublineVersion.getValidFrom())
                              .validTo(sublineVersion.getValidTo())
                              .businessOrganisation(sublineVersion.getBusinessOrganisation())
                              .build();
  }

  static SublineVersion toEntity(SublineVersionModel sublineVersionModel) {
    return SublineVersion.builder()
                         .id(sublineVersionModel.getId())
                         .swissSublineNumber(sublineVersionModel.getSwissSublineNumber())
                         .swissLineNumber(sublineVersionModel.getSwissLineNumber())
                         .status(sublineVersionModel.getStatus())
                         .type(sublineVersionModel.getType())
                         .slnid(sublineVersionModel.getSlnid())
                         .description(sublineVersionModel.getDescription())
                         .number(sublineVersionModel.getNumber())
                         .longName(sublineVersionModel.getLongName())
                         .paymentType(sublineVersionModel.getPaymentType())
                         .validFrom(sublineVersionModel.getValidFrom())
                         .validTo(sublineVersionModel.getValidTo())
                         .businessOrganisation(sublineVersionModel.getBusinessOrganisation())
                         .build();
  }
}
