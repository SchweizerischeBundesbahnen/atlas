package ch.sbb.line.directory.controller;

import ch.sbb.line.directory.api.LineVersionApi;
import ch.sbb.line.directory.api.LineVersionModel;
import ch.sbb.line.directory.api.VersionsContainer;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.repository.LineVersionRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LineVersionController implements LineVersionApi {

  private final LineVersionRepository lineVersionRepository;

  @Override
  public VersionsContainer<LineVersionModel> getLineVersions(Pageable pageable) {
    log.info("Load Versions using pageable={}", pageable);
    List<LineVersionModel> versions = lineVersionRepository.findAll(pageable)
                                                           .stream()
                                                           .map(this::toModel)
                                                           .collect(Collectors.toList());
    long totalCount = lineVersionRepository.count();
    return VersionsContainer.<LineVersionModel>builder()
                            .versions(versions)
                            .totalCount(totalCount).build();
  }

  @Override
  public LineVersionModel getLineVersion(Long id) {
    return lineVersionRepository.findById(id)
                                .map(this::toModel)
                                .orElseThrow(NotFoundExcpetion.getInstance());
  }

  @Override
  public LineVersionModel createLineVersion(LineVersionModel newVersion) {
    LineVersion createdVersion = lineVersionRepository.save(toEntity(newVersion));
    return toModel(createdVersion);
  }

  @Override
  public LineVersionModel updateLineVersion(Long id, LineVersionModel newVersion) {
    LineVersion versionToUpdate = lineVersionRepository.findById(id)
                                                       .orElseThrow(
                                                           NotFoundExcpetion.getInstance());

    versionToUpdate.setStatus(newVersion.getStatus());
    versionToUpdate.setType(newVersion.getType());
    versionToUpdate.setSlnid(newVersion.getSlnid());
    versionToUpdate.setPaymentType(newVersion.getPaymentType());
    versionToUpdate.setShortName(newVersion.getShortName());
    versionToUpdate.setAlternativeName(newVersion.getAlternativeName());
    versionToUpdate.setCombinationName(newVersion.getCombinationName());
    versionToUpdate.setLongName(newVersion.getLongName());
    versionToUpdate.setColorFontRgb(newVersion.getColorFontRgb());
    versionToUpdate.setColorBackRgb(newVersion.getColorBackRgb());
    versionToUpdate.setColorFontCmyk(newVersion.getColorFontCmyk());
    versionToUpdate.setColorBackCmyk(newVersion.getColorBackCmyk());
    versionToUpdate.setDescription(newVersion.getDescription());
    versionToUpdate.setIcon(newVersion.getIcon());
    versionToUpdate.setValidFrom(newVersion.getValidFrom());
    versionToUpdate.setValidTo(newVersion.getValidTo());
    versionToUpdate.setBusinessOrganisation(newVersion.getBusinessOrganisation());
    versionToUpdate.setComment(newVersion.getComment());
    versionToUpdate.setSwissLineNumber(newVersion.getSwissLineNumber());
    lineVersionRepository.save(versionToUpdate);

    return toModel(versionToUpdate);
  }

  @Override
  public void deleteLineVersion(Long id) {
    if (!lineVersionRepository.existsById(id)) {
      throw NotFoundExcpetion.getInstance().get();
    }
    lineVersionRepository.deleteById(id);
  }

  private LineVersionModel toModel(LineVersion lineVersion) {
    return LineVersionModel.builder()
                           .id(lineVersion.getId())
                           .status(lineVersion.getStatus())
                           .type(lineVersion.getType())
                           .slnid(lineVersion.getSlnid())
                           .paymentType(lineVersion.getPaymentType())
                           .shortName(lineVersion.getShortName())
                           .alternativeName(lineVersion.getAlternativeName())
                           .combinationName(lineVersion.getCombinationName())
                           .longName(lineVersion.getLongName())
                           .colorFontRgb(lineVersion.getColorFontRgb())
                           .colorBackRgb(lineVersion.getColorBackRgb())
                           .colorFontCmyk(
                               lineVersion.getColorFontCmyk())
                           .colorBackCmyk(lineVersion.getColorBackCmyk())
                           .description(lineVersion.getDescription())
                           .icon(lineVersion.getIcon())
                           .validFrom(lineVersion.getValidFrom())
                           .validTo(lineVersion.getValidTo())
                           .businessOrganisation(lineVersion.getBusinessOrganisation())
                           .comment(lineVersion.getComment())
                           .swissLineNumber(lineVersion.getSwissLineNumber())
                           .build();
  }

  private LineVersion toEntity(LineVersionModel lineVersionModel) {
    return LineVersion.builder()
                      .id(lineVersionModel.getId())
                      .status(lineVersionModel.getStatus())
                      .type(lineVersionModel.getType())
                      .slnid(lineVersionModel.getSlnid())
                      .paymentType(lineVersionModel.getPaymentType())
                      .shortName(lineVersionModel.getShortName())
                      .alternativeName(lineVersionModel.getAlternativeName())
                      .combinationName(lineVersionModel.getCombinationName())
                      .longName(lineVersionModel.getLongName())
                      .colorFontRgb(lineVersionModel.getColorFontRgb())
                      .colorBackRgb(lineVersionModel.getColorBackRgb())
                      .colorFontCmyk(lineVersionModel.getColorFontCmyk())
                      .colorBackCmyk(lineVersionModel.getColorBackCmyk())
                      .description(lineVersionModel.getDescription())
                      .icon(lineVersionModel.getIcon())
                      .validFrom(lineVersionModel.getValidFrom())
                      .validTo(lineVersionModel.getValidTo())
                      .businessOrganisation(lineVersionModel.getBusinessOrganisation())
                      .comment(lineVersionModel.getComment())
                      .swissLineNumber(lineVersionModel.getSwissLineNumber())
                      .build();
  }

}
