package ch.sbb.business.organisation.directory.service;

import ch.sbb.atlas.api.bodi.enumeration.TransportCompanyStatus;
import ch.sbb.business.organisation.directory.entity.TransportCompany;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransportCompanyCsvModel {

  @JsonProperty("ID")
  private Long id;

  @JsonProperty("TU-Nummer")
  private String number;

  @JsonProperty("Initialen")
  private String abbreviation;

  @JsonProperty("Namenszusatz")
  private String nameaffix;

  @JsonProperty("HR-Name/Körperschaft")
  private String businessRegisterName;

  @JsonProperty("Status TU")
  private TransportCompanyCsvStatus transportCompanyStatus;

  @JsonProperty("HR-Nr.")
  private String businessRegisterNumber;

  @JsonProperty("UID")
  private String enterpriseId;

  @JsonProperty("RICS-Code")
  private String ricsCode;

  @JsonProperty("GO-Nr.")
  private String businessOrganisationNumbers;

  @JsonProperty("Kommentar")
  private String comment;

  public TransportCompany toEntity() {
    return TransportCompany.builder()
                           .id(getId())
                           .number(getNumber())
                           .abbreviation(getAbbreviation())
                           .description(getNameaffix())
                           .businessRegisterName(getBusinessRegisterName())
                           .transportCompanyStatus(
                               getTransportCompanyStatus().toTransportCompanyStatus())
                           .businessRegisterNumber(getBusinessRegisterNumber())
                           .enterpriseId(getEnterpriseId())
                           .ricsCode(getRicsCode())
                           .businessOrganisationNumbers(getBusinessOrganisationNumbers())
                           .comment(getComment())
                           .build();
  }

  @Getter
  @RequiredArgsConstructor
  public enum TransportCompanyCsvStatus {

    @JsonProperty("1") OPERATOR,
    @JsonProperty("2") CURRENT,
    @JsonProperty("3") SUPERVISION,
    @JsonProperty("4") OPERATING_PART,
    @JsonProperty("5") LIQUIDATED,
    @JsonProperty("6") INACTIVE,
    @JsonProperty("7") FORMER_OPERATING_PART;

    public TransportCompanyStatus toTransportCompanyStatus() {
      return TransportCompanyStatus.valueOf(name());
    }

  }
}
