package ch.sbb.atlas.kafka.model.business.organisation;

import ch.sbb.atlas.kafka.model.AtlasEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SharedBusinessOrganisationUpdate implements AtlasEvent {

  private SharedBusinessOrganisationVersionModel model;
  private UpdateAction action;

}
