package ch.sbb.line.directory;

import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.line.directory.api.LineVersionModel;
import ch.sbb.line.directory.converter.CmykColorConverter;
import ch.sbb.line.directory.converter.RgbColorConverter;
import ch.sbb.line.directory.entity.Coverage;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.enumaration.CoverageType;
import ch.sbb.line.directory.enumaration.ModelType;
import ch.sbb.line.directory.repository.CoverageRepository;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import ch.sbb.line.directory.service.LineService;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@WithMockJwtAuthentication(sbbuid = "u236171")
//@ActiveProfiles("prod")
 class LineSublineCoverageCorrectionTest {

//  static {
//    System.setProperty("DB_USER", "");
//    System.setProperty("DB_PWD", "");
//  }

  @Autowired
  private CoverageRepositoryExtension coverageRepositoryExtension;

  @Autowired
  private SublineRepositoryExtension sublineRepositoryExtension;

  @Autowired
  private LineVersionRepository lineVersionRepository;

  @Autowired
  private LineService lineService;


  @Test
  void makeMainLineLongerToCoverSublines() {
    List<Coverage> incompleteSublineCoverages = coverageRepositoryExtension.findCoveragesByModelTypeAndCoverageType(
        ModelType.SUBLINE, CoverageType.INCOMPLETE);

    for (Coverage c : incompleteSublineCoverages) {
      SublineVersion sublineBySlnid;
      try {
        sublineBySlnid = sublineRepositoryExtension.findAllBySlnidOrderByValidFrom(c.getSlnid())
                                                   .stream()
                                                   .filter((sl) -> sl.getValidFrom()
                                                                     .equals(c.getValidFrom()))
                                                   .findFirst()
                                                   .orElseThrow(() -> new RuntimeException(
                                                       "Not found sublineVersion with slnid: "
                                                           + c.getSlnid()));

      } catch (RuntimeException e) {
        System.out.println(e.getMessage());
        continue;
      }

      List<SublineVersion> sublinesByMainlineSlnid = sublineRepositoryExtension.getSublineVersionByMainlineSlnid(
          sublineBySlnid.getMainlineSlnid());

      sublinesByMainlineSlnid.sort(Comparator.comparing(SublineVersion::getValidFrom));
      LocalDate earliest = sublinesByMainlineSlnid.get(0).getValidFrom();
      LocalDate last = sublinesByMainlineSlnid.get(sublinesByMainlineSlnid.size() - 1).getValidTo();

      List<LineVersion> allBySlnidOrderByValidFrom = lineVersionRepository.findAllBySlnidOrderByValidFrom(
          sublineBySlnid.getMainlineSlnid());
      if (allBySlnidOrderByValidFrom.size() != 1) {
        System.out.println("Not Found mainline or more than 1 version, will not update: "
            + sublineBySlnid.getMainlineSlnid());
        continue;
      }

      LineVersion lineVersion = allBySlnidOrderByValidFrom.get(0);

      LineVersionModel lineVersionModel = toModel(lineVersion);

      boolean updatedValidity = false;
      if (earliest.isBefore(lineVersionModel.getValidFrom())) {
        lineVersionModel.setValidFrom(earliest);
        updatedValidity = true;
      }
      if (last.isAfter(lineVersionModel.getValidTo())) {
        lineVersionModel.setValidTo(last);
        updatedValidity = true;
      }

      if (updatedValidity) {
        lineService.updateVersion(lineVersion, toEntity(lineVersionModel));
        System.out.println("Updated line: " + lineVersion.getSlnid());
      } else {
        System.out.println(
            "No dates updated on line: " + lineVersion.getSlnid() + " so line will not be updated");
      }
    }
  }

  private LineVersionModel toModel(LineVersion lineVersion) {
    return LineVersionModel.builder()
                           .id(lineVersion.getId())
                           .status(lineVersion.getStatus())
                           .lineType(lineVersion.getLineType())
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
                           .etagVersion(lineVersion.getVersion())
                           .build();
  }

  private LineVersion toEntity(LineVersionModel lineVersionModel) {
    return LineVersion.builder()
                      .id(lineVersionModel.getId())
                      .lineType(lineVersionModel.getLineType())
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
                      .version(lineVersionModel.getEtagVersion())
                      .build();
  }
}

@Repository
interface SublineRepositoryExtension extends SublineVersionRepository {

}

@Repository
interface CoverageRepositoryExtension extends CoverageRepository {

  List<Coverage> findCoveragesByModelTypeAndCoverageType(ModelType modelType,
      CoverageType coverageType);

}
