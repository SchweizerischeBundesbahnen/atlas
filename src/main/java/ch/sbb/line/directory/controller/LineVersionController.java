package ch.sbb.line.directory.controller;

import ch.sbb.line.directory.api.LineVersionApi;
import ch.sbb.line.directory.api.LineVersionModel;
import ch.sbb.line.directory.api.VersionsContainer;
import ch.sbb.line.directory.converter.CmykColorConverter;
import ch.sbb.line.directory.converter.RgbColorConverter;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.service.LineService;
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

  private final LineService lineService;

  @Override
  public VersionsContainer<LineVersionModel> getLineVersions(Pageable pageable) {
    log.info("Load Versions using pageable={}", pageable);
    List<LineVersionModel> versions = lineService.findAll(pageable)
                                                 .stream()
                                                 .map(this::toModel)
                                                 .collect(Collectors.toList());
    long totalCount = lineService.totalCount();
    return VersionsContainer.<LineVersionModel>builder()
                            .versions(versions)
                            .totalCount(totalCount).build();
  }

  @Override
  public LineVersionModel getLineVersion(Long id) {
    return lineService.findById(id)
                      .map(this::toModel)
                      .orElseThrow(NotFoundExcpetion.getInstance());
  }

  @Override
  public LineVersionModel createLineVersion(LineVersionModel newVersion) {
    LineVersion newLineVersion = toEntity(newVersion);
    LineVersion createdVersion = lineService.save(newLineVersion);
    return toModel(createdVersion);
  }

  @Override
  public LineVersionModel updateLineVersion(Long id, LineVersionModel newVersion) {
    LineVersion versionToUpdate = lineService.findById(id)
                                             .orElseThrow(
                                                 NotFoundExcpetion.getInstance());

    versionToUpdate.setStatus(newVersion.getStatus());
    versionToUpdate.setType(newVersion.getType());
    versionToUpdate.setSlnid(newVersion.getSlnid());
    versionToUpdate.setPaymentType(newVersion.getPaymentType());
    versionToUpdate.setNumber(newVersion.getNumber());
    versionToUpdate.setAlternativeName(newVersion.getAlternativeName());
    versionToUpdate.setCombinationName(newVersion.getCombinationName());
    versionToUpdate.setLongName(newVersion.getLongName());
    versionToUpdate.setColorFontRgb(
        RgbColorConverter.fromHex(newVersion.getColorFontRgb()));
    versionToUpdate.setColorBackRgb(
        RgbColorConverter.fromHex(newVersion.getColorBackRgb()));
    versionToUpdate.setColorFontCmyk(
        CmykColorConverter.fromCmykString(newVersion.getColorFontCmyk()));
    versionToUpdate.setColorBackCmyk(
        CmykColorConverter.fromCmykString(newVersion.getColorBackCmyk()));
    versionToUpdate.setDescription(newVersion.getDescription());
    versionToUpdate.setIcon(newVersion.getIcon());
    versionToUpdate.setValidFrom(newVersion.getValidFrom());
    versionToUpdate.setValidTo(newVersion.getValidTo());
    versionToUpdate.setBusinessOrganisation(newVersion.getBusinessOrganisation());
    versionToUpdate.setComment(newVersion.getComment());
    versionToUpdate.setSwissLineNumber(newVersion.getSwissLineNumber());
    lineService.save(versionToUpdate);

    return toModel(versionToUpdate);
  }

  @Override
  public void deleteLineVersion(Long id) {
    lineService.deleteById(id);
  }

  private LineVersionModel toModel(LineVersion lineVersion) {
    return LineVersionModel.builder()
                           .id(lineVersion.getId())
                           .status(lineVersion.getStatus())
                           .type(lineVersion.getType())
                           .slnid(lineVersion.getSlnid())
                           .paymentType(lineVersion.getPaymentType())
                           .number(lineVersion.getNumber())
                           .alternativeName(lineVersion.getAlternativeName())
                           .combinationName(lineVersion.getCombinationName())
                           .longName(lineVersion.getLongName())
                           .colorFontRgb(RgbColorConverter.toHex(lineVersion.getColorFontRgb()))
                           .colorBackRgb(RgbColorConverter.toHex(lineVersion.getColorBackRgb()))
                           .colorFontCmyk(CmykColorConverter.toCmykString(
                               lineVersion.getColorFontCmyk()))
                           .colorBackCmyk(
                               CmykColorConverter.toCmykString(lineVersion.getColorBackCmyk()))
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
                      .number(lineVersionModel.getNumber())
                      .alternativeName(lineVersionModel.getAlternativeName())
                      .combinationName(lineVersionModel.getCombinationName())
                      .longName(lineVersionModel.getLongName())
                      .colorFontRgb(RgbColorConverter.fromHex(lineVersionModel.getColorFontRgb()))
                      .colorBackRgb(RgbColorConverter.fromHex(lineVersionModel.getColorBackRgb()))
                      .colorFontCmyk(
                          CmykColorConverter.fromCmykString(lineVersionModel.getColorFontCmyk()))
                      .colorBackCmyk(
                          CmykColorConverter.fromCmykString(lineVersionModel.getColorBackCmyk()))
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
