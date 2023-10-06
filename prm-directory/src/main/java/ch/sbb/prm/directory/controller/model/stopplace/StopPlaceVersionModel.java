package ch.sbb.prm.directory.controller.model.stopplace;

import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.validation.DatesValidator;
import ch.sbb.prm.directory.controller.model.BasePrmVersionModel;
import ch.sbb.prm.directory.enumeration.StandardAttributeType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@FieldNameConstants
public abstract class StopPlaceVersionModel extends BasePrmVersionModel implements DatesValidator {

  @Schema(description = "Means of transport. Indicates for which means of transport a stop is intended/equipped. Mandatory for "
      + "StopPoints")
  private List<MeanOfTransport> meansOfTransport;

  @Schema(description = "Observations on the stop")
  private String freeText;

  @Schema(description = "Stop Place Address")
  private String address;

  @Schema(description = "Stop Place Zip Code")
  private String zipCode;

  @Schema(description = "Stop Place City")
  private String city;

  @NotNull
  @Schema(description = "Shuttle service or alternative public transport offer")
  private StandardAttributeType alternativeTransport;

  @Schema(description = "Information about the shuttle service or alternative public transport options")
  private String alternativeTransportCondition;

  @NotNull
  @Schema(description = "Advance registration required for assistance")
  private StandardAttributeType assistanceAvailability;

  @Schema(description = "Notes for assistance")
  private String alternativeCondition;

  @Schema(description = "Assistance from staff")
  @NotNull
  private StandardAttributeType assistanceService;

  @Schema(description = "Acoustic and tactile equipment")
  @NotNull
  private StandardAttributeType audioTicketMachine;

  @Schema(description = "Additional information")
  private String additionalInfo;

  @NotNull
  @Schema(description = "Acoustically")
  private StandardAttributeType dynamicAudioSystem;

  @NotNull
  @Schema(description = "Dynamic/optical")
  private StandardAttributeType dynamicOpticSystem;

  @Schema(description = "Information about the ticket machine")
  private String infoTicketMachine;

  @Schema(description = "Interoperable station")
  private boolean interoperable;

  @Schema(description = "Website with additional information")
  private String url;

  @NotNull
  @Schema(description = "Static/optical")
  private StandardAttributeType visualInfo;

  @NotNull
  @Schema(description = "Barrier-free")
  private StandardAttributeType wheelchairTicketMachine;

  @NotNull
  @Schema(description = "requirements fulfilled")
  private StandardAttributeType assistanceRequestFulfilled;

  @NotNull
  @Schema(description = "Ticket machine available")
  private StandardAttributeType ticketMachine;

}
