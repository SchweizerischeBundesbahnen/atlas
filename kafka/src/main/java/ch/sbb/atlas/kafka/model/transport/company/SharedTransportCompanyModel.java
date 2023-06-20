package ch.sbb.atlas.kafka.model.transport.company;

import ch.sbb.atlas.kafka.model.AtlasEvent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class SharedTransportCompanyModel implements AtlasEvent {

  @NotNull
  private Long id;

  private String number;

  private String abbreviation;

  private String description;

  private String businessRegisterName;

  private String businessRegisterNumber;
}
