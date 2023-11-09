package ch.sbb.exportservice.entity;

import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
public class StopPointVersion extends BaseEntity {

  private Long id;

  private String sloid;

  private ServicePointNumber number;

  private Set<MeanOfTransport> meansOfTransport;

  private String meansOfTransportPipeList;

  private String freeText;

  private String address;

  private String zipCode;

  private String city;

  private StandardAttributeType alternativeTransport;

  private String alternativeTransportCondition;

  private StandardAttributeType assistanceAvailability;

  private String assistanceCondition;

  private StandardAttributeType assistanceService;

  private StandardAttributeType audioTicketMachine;

  private String additionalInformation;

  private StandardAttributeType dynamicAudioSystem;

  private StandardAttributeType dynamicOpticSystem;

  private String infoTicketMachine;

  private Boolean interoperable;

  private String url;

  private StandardAttributeType visualInfo;

  private StandardAttributeType wheelchairTicketMachine;

  private StandardAttributeType assistanceRequestFulfilled;

  private StandardAttributeType ticketMachine;

  private LocalDate validFrom;

  private LocalDate validTo;

  public Set<MeanOfTransport> getMeansOfTransport() {
    if (meansOfTransport == null) {
      return new HashSet<>();
    }
    return meansOfTransport;
  }

}
