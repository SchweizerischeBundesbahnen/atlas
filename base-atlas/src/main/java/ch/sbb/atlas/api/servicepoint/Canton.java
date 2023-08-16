package ch.sbb.atlas.api.servicepoint;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@Schema(name = "Canton")
public class Canton {

  @Schema(description = "Canton number, offical Number of FSO", example = "2")
  private Integer fsoNumber;

  @Schema(description = "Canton name", example = "Bern")
  private String name;

  @Schema(description = "Canton abbreviation", example = "BE")
  private String abbreviation;

}