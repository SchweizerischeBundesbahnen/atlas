package ch.sbb.line.directory.controller;

import ch.sbb.atlas.api.lidi.SublineApiV1;
import ch.sbb.atlas.api.lidi.SublineVersionModel;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.service.SublineService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SublineControllerV1 implements SublineApiV1 {

  private final SublineService sublineService;

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
