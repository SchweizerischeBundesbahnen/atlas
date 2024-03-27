package ch.sbb.atlas.restdoc;

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