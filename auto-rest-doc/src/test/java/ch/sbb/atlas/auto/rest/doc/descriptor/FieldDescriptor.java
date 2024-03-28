package ch.sbb.atlas.auto.rest.doc.descriptor;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FieldDescriptor {

  private String name;
  private String type;
  private boolean optional;
  @Builder.Default
  private String description = "";

}