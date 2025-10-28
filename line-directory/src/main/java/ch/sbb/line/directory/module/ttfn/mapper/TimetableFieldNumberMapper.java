package ch.sbb.line.directory.module.ttfn.mapper;

import ch.sbb.atlas.api.lidi.TimetableFieldNumberModel;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberVersionModel;
import ch.sbb.line.directory.module.ttfn.entity.TimetableFieldNumber;
import ch.sbb.line.directory.module.ttfn.entity.TimetableFieldNumberVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TimetableFieldNumberMapper {

  public static TimetableFieldNumberVersionModel toModel(TimetableFieldNumberVersion version) {
    return TimetableFieldNumberVersionModel.builder()
        .id(version.getId())
        .descriptionOutwardLine1(version.getDescriptionOutwardLine1())
        .descriptionOutwardLine2(version.getDescriptionOutwardLine2())
        .descriptionOutwardLine3(version.getDescriptionOutwardLine3())
        .descriptionReturnLine1(version.getDescriptionReturnLine1())
        .descriptionReturnLine2(version.getDescriptionReturnLine2())
        .descriptionReturnLine3(version.getDescriptionReturnLine3())
        .meanOfTransport(version.getMeanOfTransport())
        .number(version.getNumber())
        .ttfnid(version.getTtfnid())
        .swissTimetableFieldNumber(version.getSwissTimetableFieldNumber())
        .status(version.getStatus())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .businessOrganisation(version.getBusinessOrganisation())
        .creator(version.getCreator())
        .creationDate(version.getCreationDate())
        .editor(version.getEditor())
        .editionDate(version.getEditionDate())
        .etagVersion(version.getVersion())
        .build();
  }

  public static TimetableFieldNumberModel toModel(TimetableFieldNumber version) {
    return TimetableFieldNumberModel.builder()
        .descriptionOutwardLine1(version.getDescriptionOutwardLine1())
        .number(version.getNumber())
        .ttfnid(version.getTtfnid())
        .swissTimetableFieldNumber(version.getSwissTimetableFieldNumber())
        .status(version.getStatus())
        .businessOrganisation(version.getBusinessOrganisation())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .build();
  }

  public static TimetableFieldNumberVersion toEntity(TimetableFieldNumberVersionModel versionModel) {
    return TimetableFieldNumberVersion.builder()
        .id(versionModel.getId())
        .descriptionOutwardLine1(versionModel.getDescriptionOutwardLine1())
        .descriptionOutwardLine2(versionModel.getDescriptionOutwardLine2())
        .descriptionOutwardLine3(versionModel.getDescriptionOutwardLine3())
        .descriptionReturnLine1(versionModel.getDescriptionReturnLine1())
        .descriptionReturnLine2(versionModel.getDescriptionReturnLine2())
        .descriptionReturnLine3(versionModel.getDescriptionReturnLine3())
        .meanOfTransport(versionModel.getMeanOfTransport())
        .number(versionModel.getNumber())
        .swissTimetableFieldNumber(versionModel.getSwissTimetableFieldNumber())
        .status(versionModel.getStatus())
        .validFrom(versionModel.getValidFrom())
        .validTo(versionModel.getValidTo())
        .businessOrganisation(versionModel.getBusinessOrganisation())
        .creationDate(versionModel.getCreationDate())
        .creator(versionModel.getCreator())
        .editionDate(versionModel.getEditionDate())
        .editor(versionModel.getEditor())
        .version(versionModel.getEtagVersion())
        .build();
  }
}
