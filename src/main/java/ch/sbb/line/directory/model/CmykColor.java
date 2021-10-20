package ch.sbb.line.directory.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CmykColor {

  @Min(0)
  @Max(100)
  private int cyan;

  @Min(0)
  @Max(100)
  private int magenta;

  @Min(0)
  @Max(100)
  private int yellow;

  @Min(0)
  @Max(100)
  private int black;
}
