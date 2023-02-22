package ch.sbb.line.directory.model.csv;

import ch.sbb.atlas.export.model.VersionCsvModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.converter.CmykColorConverter;
import ch.sbb.line.directory.converter.RgbColorConverter;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.PaymentType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"slnid", "validFrom", "validTo", "swissLineNumber", "status", "lineType",
    "paymentType", "numer", "businessOrganisation", "alternativeName", "combinationName",
    "longName", "colorFontRgb", "colorBackRgb", "colorFontCmyk", "colorBackCmyk", "icon",
    "description", "comment", "creationTime", "editionTime"})
public class LineVersionCsvModel implements VersionCsvModel {

  @JsonProperty("slnid")
  private String slnid;

  @JsonProperty("validFrom")
  private LocalDate validFrom;

  @JsonProperty("validTo")
  private LocalDate validTo;

  @JsonProperty("swissLineNumber")
  private String swissLineNumber;

  @JsonProperty("status")
  private Status status;

  @JsonProperty("lineType")
  private LineType lineType;

  @JsonProperty("paymentType")
  private PaymentType paymentType;

  @JsonProperty("numer")
  private String number;

  @JsonProperty("businessOrganisation")
  private String businessOrganisation;

  @JsonProperty("alternativeName")
  private String alternativeName;

  @JsonProperty("combinationName")
  private String combinationName;

  @JsonProperty("longName")
  private String longName;

  @JsonProperty("colorFontRgb")
  private String colorFontRgb;

  @JsonProperty("colorBackRgb")
  private String colorBackRgb;

  @JsonProperty("colorFontCmyk")
  private String colorFontCmyk;

  @JsonProperty("colorBackCmyk")
  private String colorBackCmyk;

  @JsonProperty("icon")
  private String icon;

  @JsonProperty("description")
  private String description;

  @JsonProperty("comment")
  private String comment;

  @JsonProperty("editionTime")
  private LocalDateTime editionTime;

  @JsonProperty("creationTime")
  private LocalDateTime creationTime;

  public static LineVersionCsvModel toCsvModel(LineVersion lineVersion) {
    LineVersionCsvModel lineVersionCsvModel = new LineVersionCsvModel();
    lineVersionCsvModel.setSwissLineNumber(lineVersion.getSwissLineNumber());
    lineVersionCsvModel.setSlnid(lineVersion.getSlnid());
    lineVersionCsvModel.setStatus(lineVersion.getStatus());
    lineVersionCsvModel.setLineType(lineVersion.getLineType());
    lineVersionCsvModel.setPaymentType(lineVersion.getPaymentType());
    lineVersionCsvModel.setNumber(lineVersion.getNumber());
    lineVersionCsvModel.setAlternativeName(lineVersion.getAlternativeName());
    lineVersionCsvModel.setCombinationName(lineVersion.getCombinationName());
    lineVersionCsvModel.setLongName(lineVersion.getLongName());
    lineVersionCsvModel.setColorFontRgb(RgbColorConverter.toHex(lineVersion.getColorFontRgb()));
    lineVersionCsvModel.setColorBackRgb(RgbColorConverter.toHex(lineVersion.getColorBackRgb()));
    lineVersionCsvModel.setColorBackCmyk(
        CmykColorConverter.toCmykString(lineVersion.getColorBackCmyk()));
    lineVersionCsvModel.setColorFontCmyk(
        CmykColorConverter.toCmykString(lineVersion.getColorFontCmyk()));
    lineVersionCsvModel.setIcon(lineVersion.getIcon());
    lineVersionCsvModel.setDescription(lineVersion.getDescription());
    lineVersionCsvModel.setValidFrom(lineVersion.getValidFrom());
    lineVersionCsvModel.setValidTo(lineVersion.getValidTo());
    lineVersionCsvModel.setBusinessOrganisation(lineVersion.getBusinessOrganisation());
    lineVersionCsvModel.setComment(lineVersion.getComment());
    lineVersionCsvModel.setEditionTime(lineVersion.getEditionDate());
    lineVersionCsvModel.setCreationTime(lineVersion.getCreationDate());
    return lineVersionCsvModel;
  }

}
