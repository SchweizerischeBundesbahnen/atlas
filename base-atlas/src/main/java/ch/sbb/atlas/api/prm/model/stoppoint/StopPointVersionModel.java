package ch.sbb.atlas.api.prm.model.stoppoint;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.model.BasePrmVersionModel;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.validation.DatesValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@FieldNameConstants
@Schema(name = "StopPointVersion")
public class StopPointVersionModel extends BasePrmVersionModel implements DatesValidator {

  @NotEmpty
  @Schema(description = "Means of transport. Indicates for which means of transport a stop is intended/equipped. Mandatory for "
      + "StopPoints")
  private List<MeanOfTransport> meansOfTransport;

  @Size(max = AtlasFieldLengths.LENGTH_2000)
  @Schema(description = "Observations on the stop")
  private String freeText;

  @Size(max = AtlasFieldLengths.LENGTH_2000)
  @Schema(description = "Stop Place Address")
  private String address;

  @Size(max = AtlasFieldLengths.LENGTH_50)
  @Schema(description = "Stop Place Zip Code")
  private String zipCode;

  @Size(max = AtlasFieldLengths.LENGTH_75)
  @Schema(description = "Stop Place City")
  private String city;

  private StandardAttributeType alternativeTransport;

  @Size(max = AtlasFieldLengths.LENGTH_2000)
  @Schema(description = "Information about the shuttle service or alternative public transport options")
  private String alternativeTransportCondition;

  private StandardAttributeType assistanceAvailability;

  @Size(max = AtlasFieldLengths.LENGTH_2000)
  @Schema(description = "Notes for assistance")
  private String assistanceCondition;

  private StandardAttributeType assistanceService;

  private StandardAttributeType audioTicketMachine;

  @Size(max = AtlasFieldLengths.LENGTH_2000)
  @Schema(description = "Additional information")
  private String additionalInformation;

  private StandardAttributeType dynamicAudioSystem;

  private StandardAttributeType dynamicOpticSystem;

  @Size(max = AtlasFieldLengths.LENGTH_2000)
  @Schema(description = "Information about the ticket machine")
  private String infoTicketMachine;

  @Schema(description = "Interoperable station")
  private Boolean interoperable;

  @Size(max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = "Website with additional information")
  private String url;

  private StandardAttributeType visualInfo;

  private StandardAttributeType wheelchairTicketMachine;

  private BooleanOptionalAttributeType assistanceRequestFulfilled;

  private BooleanOptionalAttributeType ticketMachine;

}
