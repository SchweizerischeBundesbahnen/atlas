package ch.sbb.atlas.export.model.prm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@FieldNameConstants
@EqualsAndHashCode
public class StopPointVersionCsvModel {

  private String sloid;

  private Integer number;

  private String freeText;

  private Integer checkDigit;

  private String address;

  private String zipCode;

  private String city;

  private String meansOfTransport;

  private String alternativeTransport;

  private String alternativeTransportCondition;

  private String assistanceAvailability;

  private String assistanceCondition;

  private String assistanceService;

  private String audioTicketMachine;

  private String additionalInformation;

  private String dynamicAudioSystem;

  private String dynamicOpticSystem;

  private String infoTicketMachine;

  private String interoperable;

  private String url;

  private String visualInfo;

  private String wheelchairTicketMachine;

  private String assistanceRequestFulfilled;

  private String ticketMachine;

  private String validFrom;

  private String validTo;

  private String creationDate;

  private String editionDate;

}
