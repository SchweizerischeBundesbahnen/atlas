package ch.sbb.line.directory.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
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
