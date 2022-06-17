package ch.sbb.business.organisation.directory.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
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
  private TransportCompanyStatus transportCompanyStatus;

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

}
