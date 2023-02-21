package ch.sbb.line.directory.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class RgbColor {

  @Min(0)
  @Max(255)
  private int red;

  @Min(0)
  @Max(255)
  private int green;

  @Min(0)
  @Max(255)
  private int blue;
}
