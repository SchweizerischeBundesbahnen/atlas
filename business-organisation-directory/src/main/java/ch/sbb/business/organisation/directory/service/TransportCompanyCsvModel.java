package ch.sbb.business.organisation.directory.service;

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

  @JsonProperty("Amtl. Bezeichnung")
  private String description;

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
                           .description(getDescription())
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

    // Betreiber
    @JsonProperty("1")
    OPERATOR,
    // Aktuell
    @JsonProperty("2")
    CURRENT,
    // Aufsicht
    @JsonProperty("3")
    SUPERVISION,
    // Betriebsteil
    @JsonProperty("4")
    OPERATING_PART,
    // Liquidiert
    @JsonProperty("5")
    LIQUIDATED,
    // Inaktiv
    @JsonProperty("6")
    INACTIVE,

    ;

    public TransportCompanyStatus toTransportCompanyStatus() {
      return TransportCompanyStatus.valueOf(name());
    }

  }

}
