package ch.sbb.exportservice.job.prm.stoppoint.model;

import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.exportservice.job.prm.BasePrmCsvModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@SuperBuilder
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
public class StopPointVersionCsvModel extends BasePrmCsvModel {

  private String sloid;

  private Integer number;

  private String freeText;

  private Integer checkDigit;

  private String address;

  private String zipCode;

  private String city;

  private String meansOfTransport;

  private String alternativeTransport;

  private String shuttleService;

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

  private boolean recordingObligation;

}
