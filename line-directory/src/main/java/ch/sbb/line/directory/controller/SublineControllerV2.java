package ch.sbb.line.directory.controller;

import ch.sbb.atlas.api.lidi.SublineApiV2;
import ch.sbb.atlas.api.lidi.SublineVersionModelV2;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.service.SublineService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SublineControllerV2 implements SublineApiV2 {

  private final SublineService sublineService;

  @Override
  public List<SublineVersionModelV2> getSublineVersion(String slnid) {
    List<SublineVersion> versions = sublineService.findSubline(slnid);
    String lineSlnid = versions.getFirst().getMainlineSlnid();
    LineVersion lineVersion = sublineService.getMainLineVersion(lineSlnid);
    return versions.stream().map(sublineVersion -> toModel(sublineVersion, lineVersion)).toList();

  }

  private SublineVersionModelV2 toModel(SublineVersion sublineVersion, LineVersion lineVersion) {
    return SublineVersionModelV2.builder()
        .id(sublineVersion.getId())
        .swissSublineNumber(sublineVersion.getSwissSublineNumber())
        .mainlineSlnid(sublineVersion.getMainlineSlnid())
        .sublineConcessionType(sublineVersion.getConcessionType())
        .lineConcessionType(lineVersion.getConcessionType())
        .mainSwissLineNumber(lineVersion.getSwissLineNumber())
        .mainShortNumber(lineVersion.getShortNumber())
        .offerCategory(lineVersion.getOfferCategory())
        .status(sublineVersion.getStatus())
        .sublineType(sublineVersion.getSublineType())
        .slnid(sublineVersion.getSlnid())
        .description(sublineVersion.getDescription())
        .longName(sublineVersion.getLongName())
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
