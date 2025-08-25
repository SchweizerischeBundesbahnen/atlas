package ch.sbb.line.directory.module.line.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @deprecated since V2.544.0
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Deprecated(forRemoval = true, since = "2.328.0")
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
