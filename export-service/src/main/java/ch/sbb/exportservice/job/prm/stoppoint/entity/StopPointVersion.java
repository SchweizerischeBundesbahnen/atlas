package ch.sbb.exportservice.job.prm.stoppoint.entity;

import static ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport.BOAT;
import static ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport.BUS;
import static ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport.CABLE_CAR;
import static ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport.CABLE_RAILWAY;
import static ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport.CHAIRLIFT;
import static ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport.ELEVATOR;
import static ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport.ON_DEMAND;
import static ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport.TRAM;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityStopPoint;
import ch.sbb.exportservice.job.prm.BasePrmEntity;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
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
public class StopPointVersion extends BasePrmEntity implements AccessibilityStopPoint {

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

  private StandardAttributeType shuttleService;

  private String alternativeTransportCondition;

  private StandardAttributeType assistanceAvailability;

  private String assistanceCondition;

  private StandardAttributeType assistanceService;

  private StandardAttributeType audioTicketMachine;

  private String additionalInformation;

  private StandardAttributeType dynamicAudioSystem;

  private StandardAttributeType dynamicOpticSystem;

  private String infoTicketMachine;

  private String interoperable;

  private String url;

  private StandardAttributeType visualInfo;

  private StandardAttributeType wheelchairTicketMachine;

  private BooleanOptionalAttributeType assistanceRequestFulfilled;

  private BooleanOptionalAttributeType ticketMachine;

  private LocalDate validFrom;

  private LocalDate validTo;

  public Set<MeanOfTransport> getMeansOfTransport() {
    if (meansOfTransport == null) {
      return new HashSet<>();
    }
    return meansOfTransport;
  }

  @Override
  public boolean isReduced() {
    return Stream.of(ELEVATOR, BUS, CHAIRLIFT, CABLE_CAR, CABLE_RAILWAY, BOAT, TRAM, ON_DEMAND)
        .anyMatch(meansOfTransport::contains);
  }
}
