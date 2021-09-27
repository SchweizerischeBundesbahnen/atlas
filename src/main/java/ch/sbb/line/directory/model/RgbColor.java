package ch.sbb.line.directory.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
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
