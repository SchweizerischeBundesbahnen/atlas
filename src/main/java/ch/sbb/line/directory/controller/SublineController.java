package ch.sbb.line.directory.controller;

import ch.sbb.line.directory.api.Container;
import ch.sbb.line.directory.api.SublineModel;
import ch.sbb.line.directory.api.SublineVersionModel;
import ch.sbb.line.directory.api.SublinenApiV1;
import ch.sbb.line.directory.entity.Subline;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.service.SublineService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SublineController implements SublinenApiV1 {

  private final SublineService sublineService;

  @Override
  public Container<SublineModel> getSublines(Pageable pageable) {
    Page<Subline> sublines = sublineService.findAll(pageable);
    return Container.<SublineModel>builder()
                    .objects(sublines.stream().map(this::toModel).collect(Collectors.toList()))
                    .totalCount(sublines.getTotalElements())
                    .build();
  }

  private SublineModel toModel(Subline sublineVersion) {
    return SublineModel.builder()
                       .swissSublineNumber(sublineVersion.getSwissSublineNumber())
                       .swissLineNumber(sublineVersion.getSwissLineNumber())
                       .status(sublineVersion.getStatus())
                       .type(sublineVersion.getType())
                       .slnid(sublineVersion.getSlnid())
                       .description(sublineVersion.getDescription())
                       .validFrom(sublineVersion.getValidFrom())
                       .validTo(sublineVersion.getValidTo())
                       .businessOrganisation(sublineVersion.getBusinessOrganisation())
                       .build();
  }

  @Override
  public List<SublineVersionModel> getSubline(String slnid) {
    return sublineService.findSubline(slnid)
                         .stream()
                         .map(this::toModel)
                         .collect(Collectors.toList());
  }

  @Override
  public SublineVersionModel getSublineVersion(Long id) {
    return sublineService.findById(id)
                         .map(this::toModel)
                         .orElseThrow(NotFoundExcpetion.getInstance());
  }

  @Override
  public SublineVersionModel createSublineVersion(SublineVersionModel newSublineVersion) {
    SublineVersion sublineVersion = toEntity(newSublineVersion);
    sublineVersion.setStatus(Status.ACTIVE);
    SublineVersion createdVersion = sublineService.save(sublineVersion);
    return toModel(createdVersion);
  }

  @Override
  public SublineVersionModel updateSublineVersion(Long id, SublineVersionModel newVersion) {
    SublineVersion versionToUpdate = sublineService.findById(id)
                                                   .orElseThrow(
                                                       NotFoundExcpetion.getInstance());
    versionToUpdate.setSwissSublineNumber(newVersion.getSwissSublineNumber());
    versionToUpdate.setSwissLineNumber(newVersion.getSwissLineNumber());
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

  private SublineVersionModel toModel(SublineVersion sublineVersion) {
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

  private SublineVersion toEntity(SublineVersionModel sublineVersionModel) {
    return SublineVersion.builder()
                         .id(sublineVersionModel.getId())
                         .swissSublineNumber(sublineVersionModel.getSwissSublineNumber())
                         .swissLineNumber(sublineVersionModel.getSwissLineNumber())
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
